package com.nailpos.nailposapi.repository;

import com.nailpos.nailposapi.dto.TopCustomerDTO;
import com.nailpos.nailposapi.model.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long>, JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByPhone(String phone);
    @Query("SELECT COUNT(c) FROM Customer c " +
            "WHERE c.createdAt BETWEEN :startDateTime AND :endDateTime")
    long countNewCustomersByDateRange(@Param("startDateTime") LocalDateTime startDateTime,
                                      @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT new com.nailpos.nailposapi.dto.TopCustomerDTO(" +
            "c.customerId, c.name, c.phone, SUM(i.finalAmount)) " +
            "FROM Invoice i JOIN i.customer c " +
            "WHERE i.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY c.customerId, c.name, c.phone " +
            "ORDER BY SUM(i.finalAmount) DESC")
    List<TopCustomerDTO> findTopSpendingCustomers(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate,
                                                  Pageable pageable);

    default List<Customer> findBySpecification(Specification<Customer> spec) {
        return findAll(spec);
    }
}
