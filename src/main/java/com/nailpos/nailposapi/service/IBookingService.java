package com.nailpos.nailposapi.service;

import com.nailpos.nailposapi.dto.BookingRequestDTO;
import com.nailpos.nailposapi.dto.UpdateBookingStatusDTO;
import com.nailpos.nailposapi.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface IBookingService {
    Page<Booking> getBookings(LocalDate startDate, LocalDate endDate, Long staffId, Pageable pageable);
    Booking getBookingById(Long id);
    Booking createBooking(BookingRequestDTO bookingDTO);
    Booking updateBookingStatus(Long id, UpdateBookingStatusDTO statusDTO);
    void deleteBooking(Long id);
}
