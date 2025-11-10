package com.nailpos.nailposapi.service;

import com.nailpos.nailposapi.dto.ExpenseRequestDTO;
import com.nailpos.nailposapi.model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface IExpenseService {

    /**
     * Lấy danh sách chi phí có phân trang và lọc
     */
    Page<Expense> getExpenses(Long staffId, LocalDate startDate, LocalDate endDate, String category, Pageable pageable);

    /**
     * Lấy chi tiết một khoản chi
     */
    Expense getExpenseById(Long id);

    /**
     * Tạo một khoản chi mới
     */
    Expense createExpense(ExpenseRequestDTO expenseDTO);

    /**
     * Cập nhật một khoản chi
     */
    Expense updateExpense(Long id, ExpenseRequestDTO expenseDTO);

    /**
     * Xóa một khoản chi
     */
    void deleteExpense(Long id);
}