package com.nailpos.nailposapi.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceDetailResponseDTO {
    private Long invoiceId;
    private CustomerResponseDTO customer; // Có thể null
    private LocalDateTime createdAt;
    private BigDecimal subTotalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private List<PaymentResponseDTO> payments;
    private List<InvoiceItemResponseDTO> items;
}