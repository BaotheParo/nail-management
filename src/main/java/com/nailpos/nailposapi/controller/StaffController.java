package com.nailpos.nailposapi.controller;

import com.nailpos.nailposapi.dto.ApiResponse;
import com.nailpos.nailposapi.dto.StaffDTO;
import com.nailpos.nailposapi.mapper.StaffMapper;
import com.nailpos.nailposapi.model.Staff;
import com.nailpos.nailposapi.service.IStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final IStaffService staffService;
    private final StaffMapper staffMapper;

    // API: GET /api/staff
    @GetMapping
    public ResponseEntity<ApiResponse<List<StaffDTO>>> getAllStaff() {
        List<Staff> staffList = staffService.getAllStaff();

        // Dùng mapper và trả về theo chuẩn ApiResponse
        List<StaffDTO> dtoList = staffMapper.toDTOList(staffList);
        return ResponseEntity.ok(ApiResponse.ok(dtoList, "Lấy danh sách nhân viên thành công"));
    }

}
