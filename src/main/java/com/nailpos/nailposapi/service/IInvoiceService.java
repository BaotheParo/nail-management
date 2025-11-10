package com.nailpos.nailposapi.service;

import com.nailpos.nailposapi.dto.InvoiceDetailResponseDTO;
import com.nailpos.nailposapi.dto.InvoiceRequestDTO;
import com.nailpos.nailposapi.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface IInvoiceService {

    /**
     * Tạo một hóa đơn mới
     * Đây là hàm @Transactional phức tạp nhất
     */
    Invoice createInvoice(InvoiceRequestDTO invoiceDTO);

    /**
     * Lấy chi tiết một hóa đơn
     */
    Invoice getInvoiceById(Long id);

    /**
     * Lấy lịch sử hóa đơn (phân trang và lọc)
     */
    Page<Invoice> getInvoices(Long customerId, Long staffId, LocalDate startDate, LocalDate endDate, Pageable pageable);
}