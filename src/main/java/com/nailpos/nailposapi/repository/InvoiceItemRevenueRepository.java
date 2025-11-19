package com.nailpos.nailposapi.repository;

import com.nailpos.nailposapi.model.InvoiceItemRevenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface InvoiceItemRevenueRepository extends JpaRepository<InvoiceItemRevenue, Long> {
    @Query("SELECT COALESCE(SUM(r.finalRevenueAfterDiscount), 0) " +
            "FROM InvoiceItemRevenue r JOIN r.invoiceItem i " + // Join InvoiceItem
            "WHERE i.invoice.createdAt BETWEEN :startDateTime AND :endDateTime")
    BigDecimal sumFinalRevenueByDateRange(@Param("startDateTime") LocalDateTime startDateTime,
                                          @Param("endDateTime") LocalDateTime endDateTime);

    /**
     * Tính tổng doanh thu thực nhận (sau giảm giá) của 1 nhân viên (cho Đối soát Q5)
     */
    @Query("SELECT COALESCE(SUM(r.finalRevenueAfterDiscount), 0) " +
            "FROM InvoiceItemRevenue r JOIN r.invoiceItem i " + // Join InvoiceItem
            "WHERE r.staff.staffId = :staffId " +
            "AND i.invoice.createdAt BETWEEN :startDateTime AND :endDateTime")
    BigDecimal sumFinalRevenueByStaffAndDateRange(@Param("staffId") Long staffId,
                                                  @Param("startDateTime") LocalDateTime startDateTime,
                                                  @Param("endDateTime") LocalDateTime endDateTime);
}