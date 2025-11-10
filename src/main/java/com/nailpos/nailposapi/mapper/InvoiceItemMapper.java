package com.nailpos.nailposapi.mapper;

import com.nailpos.nailposapi.dto.InvoiceItemResponseDTO;
import com.nailpos.nailposapi.model.InvoiceItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ServiceItemMapper.class, InvoiceItemRevenueMapper.class})
public interface InvoiceItemMapper {

    @Mapping(source = "serviceItem", target = "serviceItem") // <-- Đổi "service" -> "serviceItem"
// --- KẾT THÚC THAY ĐỔI ---
    @Mapping(source = "invoiceItemRevenues", target = "splits")
    InvoiceItemResponseDTO toResponseDTO(InvoiceItem invoiceItem);
}