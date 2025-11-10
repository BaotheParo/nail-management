package com.nailpos.nailposapi.specification;

import com.nailpos.nailposapi.model.Customer;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecification {
    public static Specification<Customer> filterBy(String search){
        return ((root, query, criteriaBuilder) -> {
            if (search == null || search.trim().isEmpty()){
                return criteriaBuilder.conjunction(); //WHERE 1=1
            }
            String searchTerm = "%" + search.toLowerCase() + "%";

            Predicate nameLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),searchTerm);
            Predicate phoneLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), searchTerm);
            return criteriaBuilder.or(nameLike,phoneLike);
        });
    }
}
