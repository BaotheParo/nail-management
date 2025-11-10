package com.nailpos.nailposapi.repository;

import com.nailpos.nailposapi.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {
    // JpaSpecificationExecutor để lọc lịch sử hóa đơn
}