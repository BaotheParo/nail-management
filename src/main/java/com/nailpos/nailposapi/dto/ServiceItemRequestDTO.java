package com.nailpos.nailposapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceItemRequestDTO {
    // Nội dung không đổi
    @NotEmpty(message = "Tên dịch vụ không được để trống")
    @Size(max = 255)
    private String name;

    @NotNull(message = "Giá dịch vụ không được để trống")
    @Positive(message = "Giá dịch vụ phải lớn hơn 0")
    private BigDecimal basePrice;

    @NotEmpty(message = "Phân loại dịch vụ không được để trống")
    @Size(max = 100)
    private String category;
}