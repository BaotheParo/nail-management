package com.nailpos.nailposapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO con: Chi tiết "bẻ giá" cho 1 nhân viên
 */
@Data
public class RevenueSplitDTO {
    @NotNull(message = "ID nhân viên không được rỗng")
    private Long staffId;

    @NotNull(message = "Số tiền bẻ giá không được rỗng")
    @Positive(message = "Số tiền bẻ giá phải > 0")
    private BigDecimal revenueAmount;
}