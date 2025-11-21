package com.nailpos.nailposapi.repository;

import com.nailpos.nailposapi.model.ServiceItem;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

// Đổi tên file và Entity
@Repository
public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long>, JpaSpecificationExecutor<ServiceItem> {
    default List<ServiceItem> findBySpecification(Specification<ServiceItem> spec) {
        return findAll(spec);
    }
}