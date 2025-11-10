package com.nailpos.nailposapi.service.impl;

import com.nailpos.nailposapi.dto.InvoiceItemRequestDTO;
import com.nailpos.nailposapi.dto.InvoiceRequestDTO;
import com.nailpos.nailposapi.dto.RevenueSplitDTO;
import com.nailpos.nailposapi.exception.InvalidDataException;
import com.nailpos.nailposapi.exception.ResourceNotFoundException;
import com.nailpos.nailposapi.model.*;
import com.nailpos.nailposapi.repository.*;
import com.nailpos.nailposapi.service.IInvoiceService;
import com.nailpos.nailposapi.specification.InvoiceSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements IInvoiceService {

    // Inject tất cả các repository cần thiết
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final InvoiceItemRevenueRepository invoiceItemRevenueRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final ServiceItemRepository serviceItemRepository;

    @Override
    @Transactional(rollbackFor = Exception.class) // Đảm bảo toàn vẹn dữ liệu
    public Invoice createInvoice(InvoiceRequestDTO dto) {

        // --- BƯỚC 1: TÍNH TOÁN TỔNG QUAN ---

        // 1a. Tính SubTotal (tổng tiền dịch vụ)
        BigDecimal subTotalAmount = dto.getItems().stream()
                .map(InvoiceItemRequestDTO::getItemPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 1b. Tính FinalAmount (tiền khách trả)
        BigDecimal finalAmount = subTotalAmount.subtract(dto.getDiscountAmount());

        // 1c. Kiểm tra logic tiền
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidDataException("Giảm giá không thể lớn hơn tổng tiền dịch vụ.");
        }

        // --- BƯỚC 2: TẠO HÓA ĐƠN CHÍNH (INVOICE) ---
        Invoice newInvoice = new Invoice();

        // 2a. Tìm và gán khách hàng (nếu có)
        if (dto.getCustomerId() != null) {
            Customer customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", dto.getCustomerId()));
            newInvoice.setCustomer(customer);
        }

        // 2b. Set các giá trị đã tính
        newInvoice.setSubTotalAmount(subTotalAmount);
        newInvoice.setDiscountAmount(dto.getDiscountAmount());
        newInvoice.setFinalAmount(finalAmount);

        // 2c. Lưu Hóa đơn chính để lấy ID
        Invoice savedInvoice = invoiceRepository.save(newInvoice);

        // --- BƯỚC 3: TẠO THANH TOÁN (PAYMENT) - (Giải quyết Q2) ---
        Payment newPayment = new Payment();
        newPayment.setInvoice(savedInvoice);
        newPayment.setAmountPaid(finalAmount); // Khách trả số tiền cuối cùng
        newPayment.setMethod(dto.getPaymentMethod());
        paymentRepository.save(newPayment);

        // --- BƯỚC 4: VÒNG LẶP XỬ LÝ DỊCH VỤ (ITEMS) VÀ "BẺ GIÁ" (REVENUE) ---
        for (InvoiceItemRequestDTO itemDto : dto.getItems()) {

            // --- THAY ĐỔI Ở ĐÂY ---
            // 4a. Tìm dịch vụ
            ServiceItem serviceItem = serviceItemRepository.findById(itemDto.getServiceId()) // <-- Đổi
                    .orElseThrow(() -> new ResourceNotFoundException("ServiceItem", "id", itemDto.getServiceId())); // <-- Đổi
            // --- KẾT THÚC THAY ĐỔI ---

            // 4b. Tạo InvoiceItem
            InvoiceItem newItem = new InvoiceItem();
            newItem.setInvoice(savedInvoice);
            // --- THAY ĐỔI Ở ĐÂY ---
            newItem.setServiceItem(serviceItem); // <-- Đổi
            // --- KẾT THÚC THAY ĐỔI ---
            newItem.setItemPrice(itemDto.getItemPrice());
            newItem.setServiceNameSnapshot(serviceItem.getName());
            InvoiceItem savedItem = invoiceItemRepository.save(newItem);

            // 4c. Tính toán Giảm giá cho item này (Giải quyết Q3 - Phần 1)
            // Tỷ lệ của item này = Giá item / Tổng tiền (trước giảm giá)
            // (Phải check subTotalAmount != 0 để tránh lỗi chia cho 0)
            BigDecimal itemRatio = (subTotalAmount.compareTo(BigDecimal.ZERO) == 0)
                    ? BigDecimal.ZERO
                    : itemDto.getItemPrice().divide(subTotalAmount, 4, RoundingMode.HALF_UP);

            // Tiền giảm giá cho item này = Tổng giảm giá * Tỷ lệ item
            BigDecimal itemDiscount = dto.getDiscountAmount().multiply(itemRatio);


            // 4d. Vòng lặp "Bẻ giá" (Giải quyết Q1)
            for (RevenueSplitDTO splitDto : itemDto.getRevenueSplits()) {
                // i. Tìm nhân viên
                Staff staff = staffRepository.findById(splitDto.getStaffId())
                        .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", splitDto.getStaffId()));

                // ii. Tính toán Giảm giá cho nhân viên này (Giải quyết Q3 - Phần 2)
                // Tỷ lệ của nhân viên = Tiền bẻ giá / Giá item
                BigDecimal splitRatio = (itemDto.getItemPrice().compareTo(BigDecimal.ZERO) == 0)
                        ? BigDecimal.ZERO
                        : splitDto.getRevenueAmount().divide(itemDto.getItemPrice(), 4, RoundingMode.HALF_UP);

                // Tiền giảm giá mà nhân viên này chịu = Tiền giảm giá của item * Tỷ lệ của nhân viên
                BigDecimal staffDiscount = itemDiscount.multiply(splitRatio);

                // iii. Doanh thu thực của nhân viên
                BigDecimal finalRevenue = splitDto.getRevenueAmount().subtract(staffDiscount);

                // iv. Ghi vào CSDL
                InvoiceItemRevenue newRevenue = new InvoiceItemRevenue();
                newRevenue.setInvoiceItem(savedItem);
                newRevenue.setStaff(staff);
                newRevenue.setRevenueAmount(splitDto.getRevenueAmount()); // Lưu tiền bẻ giá gốc
                newRevenue.setFinalRevenueAfterDiscount(finalRevenue); // Lưu tiền thực nhận

                invoiceItemRevenueRepository.save(newRevenue);
            }
        }

        // --- BƯỚC 5: TRẢ VỀ HÓA ĐƠN ĐÃ TẠO ---
        // (Nếu có lỗi, @Transactional sẽ tự động rollback tất cả)
        return savedInvoice;
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice getInvoiceById(Long id) {
        // Dùng .orElseThrow để xử lý 404
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Invoice> getInvoices(Long customerId, Long staffId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Specification<Invoice> spec = InvoiceSpecification.filterBy(customerId, staffId, startDate, endDate);
        return invoiceRepository.findAll(spec, pageable);
    }
}