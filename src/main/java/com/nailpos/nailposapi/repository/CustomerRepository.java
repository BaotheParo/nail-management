package com.nailpos.nailposapi.repository;

import com.nailpos.nailposapi.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long>, JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByPhone(String phone);
    @Query("SELECT COUNT(c) FROM Customer c " +
            "WHERE c.createdAt BETWEEN :startDateTime AND :endDateTime")
    long countNewCustomersByDateRange(@Param("startDateTime") LocalDateTime startDateTime,
                                      @Param("endDateTime") LocalDateTime endDateTime);
}
