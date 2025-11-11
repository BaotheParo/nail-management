package com.nailpos.nailposapi.mapper;

import com.nailpos.nailposapi.dto.BookingResponseDTO;
import com.nailpos.nailposapi.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, StaffMapper.class})
public interface BookingMapper {
    @Mapping(source = "customer", target = "customer")
    @Mapping(source = "staff", target = "staff")
    BookingResponseDTO toResponseDTO(Booking booking);
}
