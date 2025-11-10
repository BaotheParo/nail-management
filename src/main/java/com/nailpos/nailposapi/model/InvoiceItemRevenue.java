package com.nailpos.nailposapi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Bảng "bẻ giá" (Q1) và lưu doanh thu thực (Q3)
 * Đây là bảng chi tiết nhất.
 */
@Entity
@Table(name = "invoice_item_revenue")
@Data
public class InvoiceItemRevenue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "revenue_id")
    private Long revenueId;

    // Liên kết ngược về Dịch vụ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private InvoiceItem invoiceItem;

    // Liên kết đến Nhân viên (Ai làm)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(name = "revenue_amount", nullable = false)
    private BigDecimal revenueAmount; // Số tiền "bẻ giá" gốc (ví dụ: Ánh 30k)

    @Column(name = "final_revenue_after_discount", nullable = false)
    private BigDecimal finalRevenueAfterDiscount; // Doanh thu thực sau khi trừ giảm giá (ví dụ: Ánh 25k)
}