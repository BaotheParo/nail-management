package com.nailpos.nailposapi.mapper;

import com.nailpos.nailposapi.dto.InvoiceDetailResponseDTO;
import com.nailpos.nailposapi.model.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, PaymentMapper.class, InvoiceItemMapper.class})
public interface InvoiceMapper {

    @Mapping(source = "customer", target = "customer")
    @Mapping(source = "payments", target = "payments")
    @Mapping(source = "invoiceItems", target = "items") // Map tên field
    InvoiceDetailResponseDTO toDetailResponseDTO(Invoice invoice);
}