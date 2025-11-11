package com.nailpos.nailposapi.dto;

import com.nailpos.nailposapi.model.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateBookingStatusDTO {
    @NotNull(message = "Trạng thái mới không được để trống")
    private BookingStatus status;
}
