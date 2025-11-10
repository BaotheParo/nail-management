package com.nailpos.nailposapi.dto;

import com.nailpos.nailposapi.validation.ValidRevenueSplit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO con: Một dịch vụ trong giỏ hàng
 * Kích hoạt Custom Validation @ValidRevenueSplit
 */
@Data
@ValidRevenueSplit // <-- CUSTOM VALIDATION
public class InvoiceItemRequestDTO {

    @NotNull(message = "ID dịch vụ không được rỗng")
    private Long serviceId;

    @NotNull(message = "Giá dịch vụ không được rỗng")
    @Positive(message = "Giá dịch vụ phải > 0")
    private BigDecimal itemPrice;

    @Valid // <-- Validate lồng (nested)
    @NotEmpty(message = "Phải có ít nhất 1 người làm dịch vụ này")
    private List<RevenueSplitDTO> revenueSplits;
}