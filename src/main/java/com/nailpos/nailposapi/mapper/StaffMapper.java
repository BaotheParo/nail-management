package com.nailpos.nailposapi.mapper;

import com.nailpos.nailposapi.dto.StaffDTO;
import com.nailpos.nailposapi.model.Staff;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StaffMapper {
    StaffDTO toDTO(Staff staff);
    List<StaffDTO> toDTOList(List<Staff> staffList);
}
