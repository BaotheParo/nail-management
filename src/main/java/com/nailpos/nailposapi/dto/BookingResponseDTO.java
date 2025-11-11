package com.nailpos.nailposapi.dto;

import com.nailpos.nailposapi.model.BookingStatus;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class BookingResponseDTO {
    private Long bookingId;
    private LocalDateTime startime;
    private LocalDateTime endTime;
    private BookingStatus status;
    private String notes;
    private CustomerResponseDTO customer;
    private StaffDTO staff;
}
