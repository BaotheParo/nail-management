package com.nailpos.nailposapi.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoiceId;

    // Khách hàng có thể là null (khách vãng lai)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "sub_total_amount", nullable = false)
    private BigDecimal subTotalAmount; // Tổng tiền trước giảm giá

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount; // Số tiền giảm giá

    @Column(name = "final_amount", nullable = false)
    private BigDecimal finalAmount; // subTotal - discount

    // Một hóa đơn có nhiều dịch vụ
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> invoiceItems = new ArrayList<>();

    // Một hóa đơn có nhiều lần thanh toán (thường là 1)
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();
}