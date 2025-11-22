package com.nailpos.nailposapi.service;

import com.nailpos.nailposapi.model.Invoice;
import com.nailpos.nailposapi.model.InvoiceItem;
import com.nailpos.nailposapi.repository.InvoiceRepository;
import com.nailpos.nailposapi.specification.InvoiceSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final InvoiceRepository invoiceRepository;

    public ByteArrayInputStream exportInvoicesToExcel(LocalDate startDate, LocalDate endDate) {
        // 1. Lấy dữ liệu (Tái sử dụng Specification lọc theo ngày)
        Specification<Invoice> spec = InvoiceSpecification.filterBy(null, null, startDate, endDate);
        List<Invoice> invoices = invoiceRepository.findAll(spec);

        // 2. Khởi tạo Excel Workbook
        try (Workbook workbook = new XSSFWorkbook(); 
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Hóa Đơn");

            // --- ĐỊNH DẠNG STYLE ---
            // Font in đậm cho Header
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Format tiền tệ (VND)
            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
            
            // Format ngày giờ
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            // --- TẠO HEADER (Hàng 0) ---
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Ngày tạo", "Khách hàng", "Dịch vụ sử dụng", "Tổng tiền", "Giảm giá", "Thực thu", "TT qua"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // --- ĐIỀN DỮ LIỆU ---
            int rowIdx = 1;
            for (Invoice invoice : invoices) {
                Row row = sheet.createRow(rowIdx++);

                // ID
                row.createCell(0).setCellValue(invoice.getInvoiceId());

                // Ngày tạo
                row.createCell(1).setCellValue(invoice.getCreatedAt().format(dateFormatter));

                // Khách hàng (Xử lý null nếu là khách vãng lai không lưu tên)
                String customerName = (invoice.getCustomer() != null) ? invoice.getCustomer().getName() : "Khách vãng lai";
                row.createCell(2).setCellValue(customerName);

                // Dịch vụ (Gộp danh sách thành chuỗi: "Cắt da, Sơn gel")
                String services = invoice.getInvoiceItems().stream()
                        .map(InvoiceItem::getServiceNameSnapshot)
                        .collect(Collectors.joining(", "));
                row.createCell(3).setCellValue(services);

                // Tổng tiền (Dùng double để Excel hiểu là số)
                row.createCell(4).setCellValue(invoice.getSubTotalAmount().doubleValue());
                
                // Giảm giá
                row.createCell(5).setCellValue(invoice.getDiscountAmount().doubleValue());
                
                // Thực thu
                row.createCell(6).setCellValue(invoice.getFinalAmount().doubleValue());

                // Thanh toán qua (Lấy phương thức thanh toán đầu tiên nếu có)
                String paymentMethod = "";
                if (!invoice.getPayments().isEmpty()) {
                    paymentMethod = invoice.getPayments().get(0).getMethod();
                }
                row.createCell(7).setCellValue(paymentMethod);
            }

            // --- TỰ ĐỘNG CĂN CHỈNH ĐỘ RỘNG CỘT ---
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo file Excel: " + e.getMessage());
        }
    }
}