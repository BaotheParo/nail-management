package com.nailpos.nailposapi.repository;

import com.nailpos.nailposapi.model.InvoiceItemRevenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceItemRevenueRepository extends JpaRepository<InvoiceItemRevenue, Long> {
}