package com.nailpos.nailposapi.specification;

import com.nailpos.nailposapi.model.Invoice;
import com.nailpos.nailposapi.model.InvoiceItem;
import com.nailpos.nailposapi.model.InvoiceItemRevenue;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class InvoiceSpecification {

    public static Specification<Invoice> filterBy(Long customerId, Long staffId, LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo Khách hàng
            if (customerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("customer").get("customerId"), customerId));
            }

            // 2. Lọc theo Nhân viên (phức tạp, cần Join)
            if (staffId != null) {
                Join<Invoice, InvoiceItem> items = root.join("invoiceItems", JoinType.LEFT);
                Join<InvoiceItem, InvoiceItemRevenue> revenues = items.join("invoiceItemRevenues", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(revenues.get("staff").get("staffId"), staffId));
                query.distinct(true); // Tránh trùng lặp hóa đơn nếu 1 HĐ có 2 item cùng 1 staff
            }

            // 3. Lọc theo Ngày bắt đầu (từ 00:00:00)
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate.atStartOfDay()));
            }

            // 4. Lọc theo Ngày kết thúc (đến 23:59:59)
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate.atTime(LocalTime.MAX)));
            }

            // Sắp xếp mặc định: Hóa đơn mới nhất lên đầu
            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}