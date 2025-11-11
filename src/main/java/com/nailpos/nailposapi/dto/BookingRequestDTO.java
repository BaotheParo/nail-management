package com.nailpos.nailposapi.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class BookingRequestDTO {

    private  Long customerId;

    @NotNull (message = "Nhân viên không được để trống")
    private Long staffId;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    @Future(message = "Thời gian bắt đầu phải ở trong tương lai")
    private LocalDateTime startTime;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    @Future(message = "Thời gian kết thúc phải ở trong tương lai")
    private LocalDateTime endTime;

    private String notes;
}
