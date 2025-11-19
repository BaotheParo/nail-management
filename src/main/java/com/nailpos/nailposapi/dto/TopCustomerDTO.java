package com.nailpos.nailposapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopCustomerDTO {
    private Long customerId;
    private String customerName;
    private String phone;
    private BigDecimal totalSpent; // Tổng tiền đã chi trong khoảng thời gian
}