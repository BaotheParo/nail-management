package com.nailpos.nailposapi.specification;

import com.nailpos.nailposapi.model.Expense;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseSpecification {

    public static Specification<Expense> filterBy(Long staffId,
                                                  LocalDate startDate,
                                                  LocalDate endDate,
                                                  String category) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo Người chi (staffId)
            if (staffId != null) {
                // Tham gia (join) vào bảng Staff và lọc theo staffId
                predicates.add(criteriaBuilder.equal(root.get("paidByStaff").get("staffId"), staffId));
            }

            // 2. Lọc theo Ngày bắt đầu (từ ngày)
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("expenseDate"), startDate));
            }

            // 3. Lọc theo Ngày kết thúc (đến ngày)
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expenseDate"), endDate));
            }

            // 4. Lọc theo Phân loại (category) - dùng like cho linh hoạt
            if (StringUtils.hasText(category)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("category")), "%" + category.toLowerCase() + "%"));
            }

            // Sắp xếp mặc định theo ngày mới nhất
            query.orderBy(criteriaBuilder.desc(root.get("expenseDate")));

            // Kết hợp tất cả điều kiện bằng AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}