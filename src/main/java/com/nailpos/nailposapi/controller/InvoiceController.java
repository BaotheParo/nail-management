package com.nailpos.nailposapi.controller;

import com.nailpos.nailposapi.dto.ApiResponse;
import com.nailpos.nailposapi.dto.InvoiceDetailResponseDTO;
import com.nailpos.nailposapi.dto.InvoiceRequestDTO;
import com.nailpos.nailposapi.mapper.InvoiceMapper;
import com.nailpos.nailposapi.model.Invoice;
import com.nailpos.nailposapi.service.IInvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final IInvoiceService invoiceService;
    private final InvoiceMapper invoiceMapper; // Dùng để map dữ liệu GET trả về

    /**
     * Endpoint chính: Tạo một hóa đơn mới
     */
    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceDetailResponseDTO>> createInvoice(
            @Valid @RequestBody InvoiceRequestDTO invoiceDTO) {

        // Gọi service @Transactional để tạo hóa đơn
        Invoice newInvoice = invoiceService.createInvoice(invoiceDTO);

        // Lấy lại chi tiết hóa đơn vừa tạo để trả về
        InvoiceDetailResponseDTO dto = invoiceMapper.toDetailResponseDTO(newInvoice);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(dto, "Tạo hóa đơn thành công"));
    }

    /**
     * Endpoint xem Lịch sử Hóa đơn (có lọc và phân trang)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceDetailResponseDTO>>> getInvoices(
            // Tham số lọc
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long staffId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            // Tham số phân trang (mặc định sort theo ngày tạo)
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort
    ) {
        // Xử lý sort
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(_sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, _sort[0]));
            }
        } else {
            orders.add(new Sort.Order(sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sort[0]));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

        // Gọi service
        Page<Invoice> invoicePage = invoiceService.getInvoices(customerId, staffId, startDate, endDate, pageable);

        // Map sang DTO
        Page<InvoiceDetailResponseDTO> dtoPage = invoicePage.map(invoiceMapper::toDetailResponseDTO);

        // Trả về theo chuẩn
        return ResponseEntity.ok(ApiResponse.ok(dtoPage, "Lấy lịch sử hóa đơn thành công"));
    }

    /**
     * Endpoint xem Chi tiết 1 Hóa đơn
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceDetailResponseDTO>> getInvoiceById(@PathVariable Long id) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        InvoiceDetailResponseDTO dto = invoiceMapper.toDetailResponseDTO(invoice);
        return ResponseEntity.ok(ApiResponse.ok(dto, "Lấy chi tiết hóa đơn thành công"));
    }
}