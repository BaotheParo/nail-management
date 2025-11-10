package com.nailpos.nailposapi.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RevenueSplitResponseDTO {
    private Long revenueId;
    private StaffDTO staff; // Trả về cả object Staff
    private BigDecimal revenueAmount; // Tiền bẻ giá gốc
    private BigDecimal finalRevenueAfterDiscount; // Tiền thực nhận
}