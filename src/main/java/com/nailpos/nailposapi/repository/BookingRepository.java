package com.nailpos.nailposapi.repository;

import com.nailpos.nailposapi.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    /**
     * Tìm kiếm các lịch hẹn (không bị hủy) của một nhân viên
     * bị chồng chéo (overlap) với một khoảng thời gian cho trước.
     * Logic: (StartA < EndB) AND (EndA > StartB)
     */
    @Query("SELECT b FROM Booking b WHERE b.staff.staffId = :staffId " +
            "AND b.status != com.nailpos.nailposapi.model.BookingStatus.CANCELLED " + // <-- THÊM DẤU CÁCH Ở ĐÂY
            "AND b.startTime < :endTime AND b.endTime > :startTime")
    List<Booking> findOverlappingBookings(
            @Param("staffId") Long staffId,
            @Param("staffTime")LocalDateTime startTime,
            @Param("endTime")LocalDateTime endTime);
}
