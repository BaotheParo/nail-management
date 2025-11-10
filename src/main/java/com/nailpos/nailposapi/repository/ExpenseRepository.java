package com.nailpos.nailposapi.repository;

import com.nailpos.nailposapi.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {
    // Kế thừa JpaSpecificationExecutor là đủ cho việc lọc động
}