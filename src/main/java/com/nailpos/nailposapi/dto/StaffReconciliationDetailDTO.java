package com.nailpos.nailposapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffReconciliationDetailDTO {
    private Long staffId;
    private String staffName;
    private BigDecimal revenueGenerated;
    private BigDecimal expensePaid;
    private BigDecimal finalBalance;
}
