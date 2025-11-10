package com.nailpos.nailposapi.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

/**
 * Entity Dịch vụ (đã đổi tên từ Service -> ServiceItem)
 */
@Entity
@Table(name = "services") // Giữ nguyên tên bảng CSDL
@Data
@SQLDelete(sql = "UPDATE services SET status = 'DELETED' WHERE service_id = ?")
@Where(clause = "status = 'ACTIVE'")
public class ServiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id") // Giữ nguyên tên cột CSDL
    private Long serviceItemId; // Đổi tên trường

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Column(name = "category")
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EntityStatus status = EntityStatus.ACTIVE;
}