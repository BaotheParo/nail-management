package com.nailpos.nailposapi.specification;

import com.nailpos.nailposapi.model.Booking;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BookingSpecification {

    public static Specification<Booking> filterBy(LocalDate startDate, LocalDate endDate, Long staffId) {
        return (root, query, criteriaBuilder) -> {
            
            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo staffId nếu được cung cấp
            if (staffId != null) {
                predicates.add(criteriaBuilder.equal(root.get("staff").get("staffId"), staffId));
            }

            // Lọc theo khoảng thời gian
            // Lấy các lịch hẹn BẮT ĐẦU trong khoảng thời gian này
            if (startDate != null && endDate != null) {
                predicates.add(criteriaBuilder.between(
                        root.get("startTime"),
                        startDate.atStartOfDay(), // Từ 00:00 của ngày bắt đầu
                        endDate.atTime(LocalTime.MAX)   // Đến 23:59 của ngày kết thúc
                ));
            } else if (startDate != null) {
                // Nếu chỉ có startDate (lọc theo 1 ngày)
                 predicates.add(criteriaBuilder.between(
                        root.get("startTime"),
                        startDate.atStartOfDay(),
                        startDate.atTime(LocalTime.MAX)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}