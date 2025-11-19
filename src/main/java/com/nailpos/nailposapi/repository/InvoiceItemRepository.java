package com.nailpos.nailposapi.repository;

import com.nailpos.nailposapi.dto.TopServiceDTO;
import com.nailpos.nailposapi.model.InvoiceItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    @Query("SELECT new com.nailpos.nailposapi.dto.TopServiceDTO(" +
            "s.serviceItemId, s.name, COUNT(ii), SUM(ii.itemPrice)) " +
            "FROM InvoiceItem ii " +
            "JOIN ii.serviceItem s " +
            "JOIN ii.invoice i " +
            "WHERE i.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY s.serviceItemId, s.name " +
            "ORDER BY COUNT(ii) DESC")
    List<TopServiceDTO> findTopServices(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        Pageable pageable);
}