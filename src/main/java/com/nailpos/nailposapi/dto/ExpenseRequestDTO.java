package com.nailpos.nailposapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO dùng để TẠO MỚI (POST) hoặc CẬP NHẬT (PUT) một khoản chi.
 * Frontend chỉ cần gửi ID của người chi.
 */
@Data
public class ExpenseRequestDTO {

    @NotEmpty(message = "Nội dung chi không được để trống")
    @Size(max = 255)
    private String description;

    @NotNull(message = "Số tiền không được để trống")
    @Positive(message = "Số tiền phải lớn hơn 0")
    private BigDecimal amount;

    @NotNull(message = "Ngày chi không được để trống")
    private LocalDate expenseDate;

    private String category;

    @NotNull(message = "Phải chọn người chi tiền")
    private Long paidByStaffId;
}