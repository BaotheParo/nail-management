package com.nailpos.nailposapi.service;

import com.nailpos.nailposapi.dto.InvoiceItemRequestDTO;
import com.nailpos.nailposapi.dto.InvoiceRequestDTO;
import com.nailpos.nailposapi.dto.RevenueSplitDTO;
import com.nailpos.nailposapi.dto.ExpenseRequestDTO;
import com.nailpos.nailposapi.model.*;
import com.nailpos.nailposapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExcelImportService {

    private final IInvoiceService invoiceService;
    private final IExpenseService expenseService;
    private final CustomerRepository customerRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final InvoiceRepository invoiceRepository;
    private final StaffRepository staffRepository;

    // --- CẤU HÌNH CỘT EXCEL (Dựa trên file sheet ngay 19.11.xlsx) ---
    // Phần Hóa đơn (Bên trái)
    private static final int COL_CUSTOMER = 1; // Cột B: Tên khách
    private static final int COL_SERVICE = 2;  // Cột C: Dịch vụ
    private static final int COL_SUONG_CK = 3; // Cột D
    private static final int COL_SUONG_TM = 4; // Cột E
    private static final int COL_ANH_CK = 5;   // Cột F
    private static final int COL_ANH_TM = 6;   // Cột G

    // Phần Chi phí (Bên phải - Ước lượng dựa trên snippet)
    private static final int COL_EXPENSE_NAME = 10; // Cột K: Tên chi phí (Điện, Nước)
    private static final int COL_EXPENSE_AMOUNT = 11; // Cột L: Số tiền
    // Trong file bạn: Ánh cột 12, Sương cột 13 để đánh dấu ai trả tiền
    private static final int COL_EXPENSE_PAID_ANH = 12; 
    private static final int COL_EXPENSE_PAID_SUONG = 13;

    // Cache ánh xạ (Memory)
    private Map<String, String> customerMap = new HashMap<>();
    private Map<String, List<String>> serviceMap = new HashMap<>();

    // Load file mapping khi khởi động
    @PostConstruct
    public void init() {
        loadMappingFile("customer_mapping.txt", customerMap, false);
        loadMappingFile("service_mapping.txt", null, true);
    }

    @Transactional
    public void importExcel(MultipartFile file) throws Exception {
        // Reload lại mapping phòng trường hợp bạn vừa sửa file txt
        init(); 
        
        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        // Duyệt qua từng sheet (Mỗi sheet là 1 ngày)
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName(); // Ví dụ: "2025.11.01" hoặc "01"
            
            // Parse ngày từ tên sheet (Cần đặt tên sheet đúng format yyyy.MM.dd hoặc dd.MM.yyyy)
            // Ở đây tôi giả định tên sheet là "2025.11.01" như file csv
            LocalDate date = parseDate(sheetName); 

            if (date != null) {
                processInvoices(sheet, date);
                processExpenses(sheet, date);
            }
        }
        workbook.close();
    }

    // --- XỬ LÝ HÓA ĐƠN (LEFT SIDE) ---
    private void processInvoices(Sheet sheet, LocalDate date) {
        // Dữ liệu bắt đầu từ dòng 4 (index 3)
        for (int r = 3; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;

            String rawCustomer = getCellValue(row, COL_CUSTOMER);
            String rawService = getCellValue(row, COL_SERVICE);

            // Nếu không có tên khách -> Dừng hoặc bỏ qua (đây có thể là dòng Tổng kết)
            if (rawCustomer.isEmpty() || rawCustomer.contains("TỔNG")) continue;

            // 1. Xử lý Khách hàng (Ánh xạ)
            Customer customer = resolveCustomer(rawCustomer);

            // 2. Tính toán tiền & Staff
            BigDecimal suongCK = getNumericValue(row, COL_SUONG_CK);
            BigDecimal suongTM = getNumericValue(row, COL_SUONG_TM);
            BigDecimal anhCK = getNumericValue(row, COL_ANH_CK);
            BigDecimal anhTM = getNumericValue(row, COL_ANH_TM);
            
            BigDecimal totalAmount = suongCK.add(suongTM).add(anhCK).add(anhTM);
            if (totalAmount.compareTo(BigDecimal.ZERO) == 0) continue; // Dòng trống

            // Xác định phương thức thanh toán chính (đơn giản hóa)
            String paymentMethod = (suongCK.add(anhCK).compareTo(BigDecimal.ZERO) > 0) ? "Chuyển khoản" : "Tiền mặt";

            // Tạo danh sách người làm để chia tiền (RevenueSplit)
            List<RevenueSplitDTO> splits = new ArrayList<>();
            if (suongCK.add(suongTM).compareTo(BigDecimal.ZERO) > 0) {
                splits.add(createSplit("Sương", suongCK.add(suongTM)));
            }
            if (anhCK.add(anhTM).compareTo(BigDecimal.ZERO) > 0) {
                splits.add(createSplit("Ánh", anhCK.add(anhTM)));
            }

            // 3. Xử lý Dịch vụ (Ánh xạ & Tách Combo)
            List<ServiceItem> services = resolveServices(rawService);
            
            // Chia đều tiền cho các dịch vụ (nếu 1 dòng mapping ra nhiều dịch vụ)
            BigDecimal pricePerItem = totalAmount.divide(BigDecimal.valueOf(services.size()), 2, RoundingMode.HALF_UP);
            
            // Chia đều RevenueSplit cho các dịch vụ luôn
            List<InvoiceItemRequestDTO> itemDTOs = new ArrayList<>();
            
            for (ServiceItem s : services) {
                InvoiceItemRequestDTO itemDTO = new InvoiceItemRequestDTO();
                itemDTO.setServiceId(s.getServiceItemId());
                itemDTO.setItemPrice(pricePerItem);
                
                // Chia lại tiền split theo tỷ lệ (để tổng split = pricePerItem)
                List<RevenueSplitDTO> itemSplits = new ArrayList<>();
                for (RevenueSplitDTO originalSplit : splits) {
                    BigDecimal splitAmount = originalSplit.getRevenueAmount()
                            .divide(BigDecimal.valueOf(services.size()), 2, RoundingMode.HALF_UP);
                    RevenueSplitDTO newSplit = new RevenueSplitDTO();
                    newSplit.setStaffId(originalSplit.getStaffId());
                    newSplit.setRevenueAmount(splitAmount);
                    itemSplits.add(newSplit);
                }
                itemDTO.setRevenueSplits(itemSplits);
                itemDTOs.add(itemDTO);
            }

            // 4. Tạo Invoice
            InvoiceRequestDTO invoiceDTO = new InvoiceRequestDTO();
            invoiceDTO.setCustomerId(customer.getCustomerId());
            invoiceDTO.setDiscountAmount(BigDecimal.ZERO);
            invoiceDTO.setPaymentMethod(paymentMethod);
            invoiceDTO.setItems(itemDTOs);

            try {
                Invoice savedInvoice = invoiceService.createInvoice(invoiceDTO);
                // Quan trọng: Update lại ngày created_at cho đúng với Excel
                savedInvoice.setCreatedAt(date.atTime(LocalTime.now()));
                invoiceRepository.save(savedInvoice);
            } catch (Exception e) {
                System.err.println("Lỗi dòng " + r + ": " + e.getMessage());
            }
        }
    }

    // --- XỬ LÝ CHI PHÍ (RIGHT SIDE) ---
    private void processExpenses(Sheet sheet, LocalDate date) {
        for (int r = 3; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;

            String expenseName = getCellValue(row, COL_EXPENSE_NAME);
            BigDecimal amount = getNumericValue(row, COL_EXPENSE_AMOUNT);

            if (expenseName.isEmpty() || amount.compareTo(BigDecimal.ZERO) == 0) continue;
            if (expenseName.contains("TỔNG")) break; // Dừng khi gặp tổng

            // Xác định ai chi tiền (dựa vào cột Ánh/Sương có dữ liệu không)
            // Logic: Nếu cột Ánh (12) có số liệu khác 0 -> Ánh trả. Ngược lại check Sương.
            Long paidById = null;
            if (getNumericValue(row, COL_EXPENSE_PAID_ANH).abs().compareTo(BigDecimal.ZERO) > 0) {
                paidById = getStaffIdByName("Ánh");
            } else if (getNumericValue(row, COL_EXPENSE_PAID_SUONG).abs().compareTo(BigDecimal.ZERO) > 0) {
                paidById = getStaffIdByName("Sương");
            }

            if (paidById != null) {
                ExpenseRequestDTO expenseDTO = new ExpenseRequestDTO();
                expenseDTO.setDescription(expenseName);
                expenseDTO.setAmount(amount.abs()); // Lấy trị tuyệt đối
                expenseDTO.setExpenseDate(date);
                expenseDTO.setPaidByStaffId(paidById);
                expenseDTO.setCategory("Chi phí Excel");
                
                expenseService.createExpense(expenseDTO);
            }
        }
    }

    // --- HELPER METHODS ---

    private Customer resolveCustomer(String rawName) {
        String cleanName = rawName.trim().toLowerCase();
        String standardName = customerMap.getOrDefault(cleanName, rawName); // Map hoặc giữ nguyên
        
        // Tìm trong DB, ko có thì tạo mới
        return customerRepository.findBySpecification(
                (root, query, cb) -> cb.like(cb.lower(root.get("name")), standardName.toLowerCase())
        ).stream().findFirst().orElseGet(() -> {
            Customer newC = new Customer();
            newC.setName(standardName); // Lưu tên chuẩn (vd: Nguyễn Trang)
            return customerRepository.save(newC);
        });
    }

    private List<ServiceItem> resolveServices(String rawService) {
        String cleanService = rawService.trim().toLowerCase();
        List<String> standardNames = serviceMap.get(cleanService);
        
        if (standardNames == null) {
            standardNames = List.of(rawService); // Không có map thì dùng chính nó
        }

        List<ServiceItem> result = new ArrayList<>();
        for (String name : standardNames) {
            ServiceItem item = serviceItemRepository.findBySpecification(
                (root, query, cb) -> cb.like(cb.lower(root.get("name")), name.toLowerCase())
            ).stream().findFirst().orElseGet(() -> {
                ServiceItem newS = new ServiceItem();
                newS.setName(name);
                newS.setBasePrice(BigDecimal.ZERO); // Giá tạm
                newS.setCategory("Dịch vụ Excel");
                return serviceItemRepository.save(newS);
            });
            result.add(item);
        }
        return result;
    }

    private RevenueSplitDTO createSplit(String staffName, BigDecimal amount) {
        RevenueSplitDTO split = new RevenueSplitDTO();
        split.setStaffId(getStaffIdByName(staffName));
        split.setRevenueAmount(amount);
        return split;
    }

    private Long getStaffIdByName(String name) {
        // Tìm staff theo tên gần đúng
        return staffRepository.findAll().stream()
                .filter(s -> s.getName().toLowerCase().contains(name.toLowerCase()))
                .findFirst()
                .map(Staff::getStaffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên: " + name));
    }

    // Đọc file txt mapping
    private void loadMappingFile(String fileName, Map<String, String> map, boolean isService) {
        try {
            ClassPathResource resource = new ClassPathResource(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    String key = parts[0].trim().toLowerCase();
                    if (isService) {
                        List<String> values = Arrays.asList(parts[1].split(","));
                        values.replaceAll(String::trim);
                        serviceMap.put(key, values);
                    } else {
                        customerMap.put(key, parts[1].trim());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Không đọc được file mapping: " + fileName + " (Có thể bỏ qua nếu không cần)");
        }
    }

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> "";
        };
    }

    private BigDecimal getNumericValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return BigDecimal.ZERO;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return BigDecimal.valueOf(cell.getNumericCellValue());
            } else if (cell.getCellType() == CellType.STRING) {
                // Xử lý chuỗi kiểu "-100" hoặc "100.000"
                String val = cell.getStringCellValue().replace(",", "").replace(".", "");
                if (val.trim().isEmpty()) return BigDecimal.ZERO;
                return new BigDecimal(val);
            }
        } catch (Exception e) { return BigDecimal.ZERO; }
        return BigDecimal.ZERO;
    }
    
    private LocalDate parseDate(String sheetName) {
        try {
            // Thử các định dạng: 2025.11.01 hoặc 01.11.2025
            if (sheetName.matches("\\d{4}\\.\\d{2}\\.\\d{2}")) {
                 return LocalDate.parse(sheetName, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            }
            // Thêm các định dạng khác nếu cần
        } catch (Exception e) {
            return LocalDate.now(); // Fallback
        }
        return LocalDate.now();
    }
}