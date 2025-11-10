package com.nailpos.nailposapi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO dùng để TRẢ VỀ cho client.
 * Bao gồm cả một object StaffDTO đầy đủ (đã map)
 * để frontend hiển thị tên "Ánh" hoặc "Sương" mà không cần gọi API phụ.
 */
@Data
public class ExpenseResponseDTO {

    private Long expenseId;
    private String description;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String category;

    // Trả về cả object StaffDTO để tiện cho frontend
    private StaffDTO staff;
}