package com.nailpos.nailposapi.mapper;

import com.nailpos.nailposapi.dto.CustomerRequestDTO;
import com.nailpos.nailposapi.dto.CustomerResponseDTO;
import com.nailpos.nailposapi.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerMapper {
    // Chuyển từ Entity sang DTO Response
    CustomerResponseDTO toResponseDTO(Customer customer);

    // Chuyển từ DTO Request sang Entity (khi tạo mới)
    Customer toEntity(CustomerRequestDTO requestDTO);

    // Cập nhật Entity từ DTO (khi update)
    void updateFromDTO(CustomerRequestDTO requestDTO, @MappingTarget Customer customer);

}
