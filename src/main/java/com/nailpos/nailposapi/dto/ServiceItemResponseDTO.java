package com.nailpos.nailposapi.dto;

import lombok.Data;
import java.math.BigDecimal;

// Đổi tên file
@Data
public class ServiceItemResponseDTO {
    private Long serviceItemId; // <-- Đã đổi tên trường
    private String name;
    private BigDecimal basePrice;
    private String category;
    private String status;
}