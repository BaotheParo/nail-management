package com.nailpos.nailposapi.service.impl;

import com.nailpos.nailposapi.dto.ExpenseRequestDTO;
import com.nailpos.nailposapi.exception.ResourceNotFoundException;
import com.nailpos.nailposapi.mapper.ExpenseMapper;
import com.nailpos.nailposapi.model.Expense;
import com.nailpos.nailposapi.model.Staff;
import com.nailpos.nailposapi.repository.ExpenseRepository;
import com.nailpos.nailposapi.repository.StaffRepository;
import com.nailpos.nailposapi.service.IExpenseService;
import com.nailpos.nailposapi.specification.ExpenseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements IExpenseService {

    private final ExpenseRepository expenseRepository;
    private final StaffRepository staffRepository; // Cần repo này để tìm người chi
    private final ExpenseMapper expenseMapper;

    @Override
    @Transactional(readOnly = true) // readOnly = true để tối ưu các truy vấn GET
    public Page<Expense> getExpenses(Long staffId, LocalDate startDate, LocalDate endDate, String category, Pageable pageable) {
        Specification<Expense> spec = ExpenseSpecification.filterBy(staffId, startDate, endDate, category);
        return expenseRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
    }

    @Override
    @Transactional // Bật Transaction cho nghiệp vụ C/U/D
    public Expense createExpense(ExpenseRequestDTO expenseDTO) {
        // 1. Tìm Staff (người chi)
        Staff paidByStaff = staffRepository.findById(expenseDTO.getPaidByStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", expenseDTO.getPaidByStaffId()));

        // 2. Map DTO -> Entity
        Expense newExpense = expenseMapper.toEntity(expenseDTO);

        // 3. Gán liên kết Staff vào Expense
        newExpense.setPaidByStaff(paidByStaff);

        // 4. Lưu vào CSDL
        return expenseRepository.save(newExpense);
    }

    @Override
    @Transactional
    public Expense updateExpense(Long id, ExpenseRequestDTO expenseDTO) {
        // 1. Tìm khoản chi
        Expense existingExpense = getExpenseById(id); // Đã bao gồm check 404

        // 2. Tìm Staff (nếu người chi thay đổi)
        Staff paidByStaff = staffRepository.findById(expenseDTO.getPaidByStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", expenseDTO.getPaidByStaffId()));

        // 3. Map DTO -> Entity (cập nhật các trường)
        expenseMapper.updateFromDTO(expenseDTO, existingExpense);

        // 4. Gán lại liên kết Staff
        existingExpense.setPaidByStaff(paidByStaff);

        // 5. Lưu vào CSDL
        return expenseRepository.save(existingExpense);
    }

    @Override
    @Transactional
    public void deleteExpense(Long id) {
        // Kiểm tra tồn tại trước khi xóa
        Expense expense = getExpenseById(id);

        // Thực hiện xóa (Hard delete, theo kế hoạch)
        expenseRepository.delete(expense);
    }
}