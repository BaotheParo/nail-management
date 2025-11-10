package com.nailpos.nailposapi.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class InvoiceItemResponseDTO {
    private Long itemId;
    private ServiceItemResponseDTO serviceItem; // Trả về cả object Service
    private BigDecimal itemPrice;
    private List<RevenueSplitResponseDTO> splits; // Danh sách người làm
}