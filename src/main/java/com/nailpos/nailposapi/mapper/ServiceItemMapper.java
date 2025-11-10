package com.nailpos.nailposapi.mapper;

import com.nailpos.nailposapi.dto.ServiceItemRequestDTO;
import com.nailpos.nailposapi.dto.ServiceItemResponseDTO;
import com.nailpos.nailposapi.model.ServiceItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServiceItemMapper {

    // Map trường ID
    @Mapping(source = "serviceItemId", target = "serviceItemId")
    ServiceItemResponseDTO toResponseDTO(ServiceItem serviceItem);

    ServiceItem toEntity(ServiceItemRequestDTO requestDTO);

    void updateFromDTO(ServiceItemRequestDTO requestDTO, @MappingTarget ServiceItem serviceItem);
}