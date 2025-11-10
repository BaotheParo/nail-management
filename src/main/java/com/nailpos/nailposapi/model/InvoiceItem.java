package com.nailpos.nailposapi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice_items")
@Data
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    // Liên kết ngược về Hóa đơn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    // Dịch vụ được chọn (ví dụ: "Cắt da")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceItem serviceItem; // <-- Đổi từ Service -> ServiceItem

    @Column(name = "service_name_snapshot", nullable = false)
    private String serviceNameSnapshot;

    @Column(name = "item_price", nullable = false)
    private BigDecimal itemPrice; // Giá bán thực tế của dịch vụ này

    // Một dịch vụ (item) có thể được "bẻ giá" cho nhiều nhân viên
    @OneToMany(mappedBy = "invoiceItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItemRevenue> invoiceItemRevenues = new ArrayList<>();
}