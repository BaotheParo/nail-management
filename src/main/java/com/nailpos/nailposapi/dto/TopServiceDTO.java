package com.nailpos.nailposapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopServiceDTO {
    private Long serviceId;
    private String serviceName;
    private Long usageCount;       // Số lần được book
    private BigDecimal totalRevenue; // Tổng doanh thu từ dịch vụ này
}