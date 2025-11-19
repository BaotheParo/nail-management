package com.nailpos.nailposapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KpiSummaryDTO {
    private BigDecimal totalRevenue; // Tổng doanh thu (sau giảm giá)
    private BigDecimal totalExpenses; // Tổng chi phí
    private BigDecimal netProfit; // Lợi nhuận = Doanh thu - Chi phí
    private long totalInvoices; // Tổng số hóa đơn
    private long newCustomers; // Số lượng khách hàng mới
}
