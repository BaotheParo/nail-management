package com.nailpos.nailposapi.mapper;

import com.nailpos.nailposapi.dto.RevenueSplitResponseDTO;
import com.nailpos.nailposapi.model.InvoiceItemRevenue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {StaffMapper.class}) // Dùng StaffMapper
public interface InvoiceItemRevenueMapper {
    @Mapping(source = "staff", target = "staff")
    RevenueSplitResponseDTO toResponseDTO(InvoiceItemRevenue revenue);
}