package com.nailpos.nailposapi.controller;


import com.nailpos.nailposapi.dto.ApiResponse;
import com.nailpos.nailposapi.dto.CustomerRequestDTO;
import com.nailpos.nailposapi.dto.CustomerResponseDTO;
import com.nailpos.nailposapi.mapper.CustomerMapper;
import com.nailpos.nailposapi.model.Customer;
import com.nailpos.nailposapi.service.ICustomerService;
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
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final ICustomerService customerService;
    private final CustomerMapper customerMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponseDTO>>> getCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "customerId,asc") String[] sort,
            @RequestParam(required = false) String search
    ) {
        // Xử lý sort (tương tự như cũ)
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

        Page<Customer> customerPage = customerService.getCustomers(search, pageable);

        Page<CustomerResponseDTO> dtoPage = customerPage.map(customerMapper::toResponseDTO);

        return ResponseEntity.ok(ApiResponse.ok(dtoPage, "Lấy danh sách khách hàng thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        CustomerResponseDTO dto = customerMapper.toResponseDTO(customer);
        return ResponseEntity.ok(ApiResponse.ok(dto, "Lấy chi tiết khách hàng thành công"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> createCustomer(
            @Valid @RequestBody CustomerRequestDTO customerDTO) {

        Customer newCustomer = customerService.createCustomer(customerDTO);
        CustomerResponseDTO dto = customerMapper.toResponseDTO(newCustomer);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(dto, "Tạo khách hàng mới thành công"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO customerDTO) {

        Customer updatedCustomer = customerService.updateCustomer(id, customerDTO);
        CustomerResponseDTO dto = customerMapper.toResponseDTO(updatedCustomer);

        return ResponseEntity.ok(ApiResponse.ok(dto, "Cập nhật khách hàng thành công"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa khách hàng thành công (soft delete)"));
    }
}
