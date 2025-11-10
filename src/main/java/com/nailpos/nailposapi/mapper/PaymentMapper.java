package com.nailpos.nailposapi.mapper;

import com.nailpos.nailposapi.dto.PaymentResponseDTO;
import com.nailpos.nailposapi.model.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponseDTO toResponseDTO(Payment payment);
}