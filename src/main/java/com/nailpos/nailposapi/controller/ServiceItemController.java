package com.nailpos.nailposapi.controller;

import com.nailpos.nailposapi.dto.ApiResponse;
import com.nailpos.nailposapi.dto.ServiceItemRequestDTO;
import com.nailpos.nailposapi.dto.ServiceItemResponseDTO;
import com.nailpos.nailposapi.mapper.ServiceItemMapper;
import com.nailpos.nailposapi.model.ServiceItem;
import com.nailpos.nailposapi.service.IServiceItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/service-items") // <-- Đổi đường dẫn API
@RequiredArgsConstructor
public class ServiceItemController {

    private final IServiceItemService serviceItemService;
    private final ServiceItemMapper serviceItemMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceItemResponseDTO>>> getServiceItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "serviceItemId,asc") String[] sort, // <-- Đổi tên trường sort
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category
    ) {
        // Xử lý sort
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(_sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, _sort[0]));
            }
        } else {
            orders.add(new Sort.Order(sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sort[0]));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

        Page<ServiceItem> servicePage = serviceItemService.getServiceItems(search, category, pageable);

        Page<ServiceItemResponseDTO> dtoPage = servicePage.map(serviceItemMapper::toResponseDTO);

        return ResponseEntity.ok(ApiResponse.ok(dtoPage, "Lấy danh sách dịch vụ thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceItemResponseDTO>> getServiceItemById(@PathVariable Long id) {
        ServiceItem serviceItem = serviceItemService.getServiceItemById(id);
        ServiceItemResponseDTO dto = serviceItemMapper.toResponseDTO(serviceItem);
        return ResponseEntity.ok(ApiResponse.ok(dto, "Lấy chi tiết dịch vụ thành công"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceItemResponseDTO>> createServiceItem(
            @Valid @RequestBody ServiceItemRequestDTO serviceDTO) {

        ServiceItem newServiceItem = serviceItemService.createServiceItem(serviceDTO);
        ServiceItemResponseDTO dto = serviceItemMapper.toResponseDTO(newServiceItem);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(dto, "Tạo dịch vụ mới thành công"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceItemResponseDTO>> updateServiceItem(
            @PathVariable Long id,
            @Valid @RequestBody ServiceItemRequestDTO serviceDTO) {

        ServiceItem updatedServiceItem = serviceItemService.updateServiceItem(id, serviceDTO);
        ServiceItemResponseDTO dto = serviceItemMapper.toResponseDTO(updatedServiceItem);

        return ResponseEntity.ok(ApiResponse.ok(dto, "Cập nhật dịch vụ thành công"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteServiceItem(@PathVariable Long id) {
        serviceItemService.deleteServiceItem(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa dịch vụ thành công (soft delete)"));
    }
}