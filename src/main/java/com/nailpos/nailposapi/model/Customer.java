package com.nailpos.nailposapi.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.Date;

@Entity
@Table(name = "customers")
@Data
@SQLDelete(sql = "UPDATE customers SET status = 'DELETED' WHERE customer_id = ?") // <-- SOFT DELETE (1)
@Where(clause = "status = 'ACTIVE'") // <-- SOFT DELETE (2)
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name="phone", unique = true)
    private String phone;

    @Column(name = "notes")
    private String notes;

    @Enumerated(EnumType.STRING) // <-- SOFT DELETE (3)
    @Column(name = "status", nullable = false)
    private EntityStatus status = EntityStatus.ACTIVE;

    // --- PROJECTIONS (Tải dữ liệu tóm tắt) ---

    // (Giả sử bảng invoices có cột final_amount và customer_id)
    @Formula("(SELECT COALESCE(SUM(i.final_amount), 0) FROM invoices i WHERE i.customer_id = customer_id)")
    private Double totalSpent;

    // (Giả sử bảng invoices có cột created_at và customer_id)
    @Formula("(SELECT MAX(i.created_at) FROM invoices i WHERE i.customer_id = customer_id)")
    private Date lastVisitDate;
}
