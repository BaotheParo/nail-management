package com.nailpos.nailposapi.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {
    private Long paymentId;
    private BigDecimal amountPaid;
    private String method;
    private LocalDateTime paymentDate;
}