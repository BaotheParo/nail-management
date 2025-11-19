package com.nailpos.nailposapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationReportDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue; // Tổng doanh thu toàn tiệm
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;
    private BigDecimal expenseSharePerStaff;
    private List<StaffReconciliationDetailDTO> details;
}
