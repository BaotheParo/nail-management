package com.nailpos.nailposapi.mapper;

import com.nailpos.nailposapi.dto.ExpenseRequestDTO;
import com.nailpos.nailposapi.dto.ExpenseResponseDTO;
import com.nailpos.nailposapi.model.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Interface MapStruct để map dữ liệu Expense.
 * Báo cho MapStruct dùng StaffMapper (đã tạo) để map Staff <-> StaffDTO.
 */
@Mapper(componentModel = "spring",
        uses = {StaffMapper.class}, // Rất quan trọng: Tái sử dụng StaffMapper
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExpenseMapper {

    /**
     * Map từ Entity Expense -> ExpenseResponseDTO
     * MapStruct sẽ tự động dùng StaffMapper để map expense.paidByStaff -> expenseResponseDTO.staff
     */
    @Mapping(source = "paidByStaff", target = "staff")
    ExpenseResponseDTO toResponseDTO(Expense expense);

    /**
     * Map từ ExpenseRequestDTO -> Entity Expense
     * Bỏ qua paidByStaffId vì chúng ta sẽ xử lý thủ công trong Service.
     */
    @Mapping(target = "paidByStaff", ignore = true)
    Expense toEntity(ExpenseRequestDTO requestDTO);

    /**
     * Cập nhật Entity từ DTO (dùng cho PUT)
     * Bỏ qua paidByStaffId vì chúng ta sẽ xử lý thủ công trong Service.
     */
    @Mapping(target = "paidByStaff", ignore = true)
    void updateFromDTO(ExpenseRequestDTO requestDTO, @MappingTarget Expense expense);
}