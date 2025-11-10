package com.nailpos.nailposapi.controller;

import com.nailpos.nailposapi.dto.ApiResponse;
import com.nailpos.nailposapi.dto.ExpenseRequestDTO;
import com.nailpos.nailposapi.dto.ExpenseResponseDTO;
import com.nailpos.nailposapi.mapper.ExpenseMapper;
import com.nailpos.nailposapi.model.Expense;
import com.nailpos.nailposapi.service.IExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final IExpenseService expenseService;
    private final ExpenseMapper expenseMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponseDTO>>> getExpenses(
            // Tham số lọc
            @RequestParam(required = false) Long staffId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String category,

            // Tham số phân trang và sắp xếp
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "expenseDate,desc") String[] sort
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

        // Gọi service
        Page<Expense> expensePage = expenseService.getExpenses(staffId, startDate, endDate, category, pageable);

        // Map sang DTO
        Page<ExpenseResponseDTO> dtoPage = expensePage.map(expenseMapper::toResponseDTO);

        // Trả về theo chuẩn ApiResponse (có phân trang)
        return ResponseEntity.ok(ApiResponse.ok(dtoPage, "Lấy danh sách chi phí thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponseDTO>> getExpenseById(@PathVariable Long id) {
        Expense expense = expenseService.getExpenseById(id);
        ExpenseResponseDTO dto = expenseMapper.toResponseDTO(expense);
        return ResponseEntity.ok(ApiResponse.ok(dto, "Lấy chi tiết chi phí thành công"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponseDTO>> createExpense(
            @Valid @RequestBody ExpenseRequestDTO expenseDTO) { // Kích hoạt Validation

        Expense newExpense = expenseService.createExpense(expenseDTO);
        ExpenseResponseDTO dto = expenseMapper.toResponseDTO(newExpense);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(dto, "Tạo chi phí mới thành công"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponseDTO>> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequestDTO expenseDTO) { // Kích hoạt Validation

        Expense updatedExpense = expenseService.updateExpense(id, expenseDTO);
        ExpenseResponseDTO dto = expenseMapper.toResponseDTO(updatedExpense);

        return ResponseEntity.ok(ApiResponse.ok(dto, "Cập nhật chi phí thành công"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa chi phí thành công"));
    }
}