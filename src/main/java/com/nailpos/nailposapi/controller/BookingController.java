package com.nailpos.nailposapi.controller;

import com.nailpos.nailposapi.dto.ApiResponse;
import com.nailpos.nailposapi.dto.BookingRequestDTO;
import com.nailpos.nailposapi.dto.BookingResponseDTO;
import com.nailpos.nailposapi.dto.UpdateBookingStatusDTO;
import com.nailpos.nailposapi.mapper.BookingMapper;
import com.nailpos.nailposapi.model.Booking;
import com.nailpos.nailposapi.service.IBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final IBookingService bookingService;
    private final BookingMapper bookingMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getBookings(
            @RequestParam @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long staffId,
            @PageableDefault(size = 100)Pageable pageable){
        Page<Booking> bookingPage = bookingService.getBookings(startDate, endDate,staffId,pageable);
        Page<BookingResponseDTO> dtoPage = bookingPage.map(bookingMapper::toResponseDTO);
        return ResponseEntity.ok(ApiResponse.ok(dtoPage,"Lấy danh sách lịch hẹn thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getBookingById(@PathVariable Long id){
        Booking booking = bookingService.getBookingById(id);
        BookingResponseDTO dto = bookingMapper.toResponseDTO(booking);
        return ResponseEntity.ok(ApiResponse.ok(dto,"Lấy chi tiết lịch hẹn thành công"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponseDTO>> creteBooking(
            @Valid @RequestBody BookingRequestDTO bookingDTO
            ){
        Booking newBooking = bookingService.createBooking(bookingDTO);
        BookingResponseDTO dto = bookingMapper.toResponseDTO(newBooking);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(dto,"Tạo lịch hẹn thành công"));
    }

    /**
     * Cập nhật trạng thái của lịch hẹn (ví dụ: Hủy hoặc Hoàn thành)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updateBookingStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookingStatusDTO statusDTO) {

        Booking updatedBooking = bookingService.updateBookingStatus(id, statusDTO);
        BookingResponseDTO dto = bookingMapper.toResponseDTO(updatedBooking);

        return ResponseEntity.ok(ApiResponse.ok(dto, "Cập nhật trạng thái thành công"));
    }

    /**
     * Xóa 1 lịch hẹn
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa lịch hẹn thành công"));
    }
}
