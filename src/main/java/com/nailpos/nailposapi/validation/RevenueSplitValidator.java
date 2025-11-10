package com.nailpos.nailposapi.validation;

import com.nailpos.nailposapi.dto.InvoiceItemRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/**
 * Class logic cho @ValidRevenueSplit
 */
public class RevenueSplitValidator implements ConstraintValidator<ValidRevenueSplit, InvoiceItemRequestDTO> {

    @Override
    public boolean isValid(InvoiceItemRequestDTO itemDto, ConstraintValidatorContext context) {
        if (itemDto == null || itemDto.getRevenueSplits() == null || itemDto.getItemPrice() == null) {
            return true; // Bỏ qua nếu null (để @NotNull xử lý)
        }

        // Tính tổng tiền "bẻ giá"
        BigDecimal totalSplitAmount = itemDto.getRevenueSplits().stream()
                .map(split -> split.getRevenueAmount() != null ? split.getRevenueAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // So sánh tổng tiền bẻ giá với giá của dịch vụ
        // Dùng compareTo() cho BigDecimal
        return itemDto.getItemPrice().compareTo(totalSplitAmount) == 0;
    }
}