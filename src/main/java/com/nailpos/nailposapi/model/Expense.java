package com.nailpos.nailposapi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Data
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long expenseId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "category")
    private String category;

    /**
     * Liên kết quan trọng: Ai là người chi tiền?
     * nullable = false đảm bảo mọi khoản chi phải có người chi.
     * FetchType.LAZY giúp tối ưu hiệu năng, chỉ tải Staff khi cần.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by_staff_id", nullable = false)
    private Staff paidByStaff;
}