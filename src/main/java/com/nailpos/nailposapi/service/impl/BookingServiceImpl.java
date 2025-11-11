package com.nailpos.nailposapi.service.impl;

import com.nailpos.nailposapi.dto.BookingRequestDTO;
import com.nailpos.nailposapi.dto.UpdateBookingStatusDTO;
import com.nailpos.nailposapi.exception.InvalidDataException;
import com.nailpos.nailposapi.exception.ResourceNotFoundException;
import com.nailpos.nailposapi.model.Booking;
import com.nailpos.nailposapi.model.BookingStatus;
import com.nailpos.nailposapi.model.Customer;
import com.nailpos.nailposapi.model.Staff;
import com.nailpos.nailposapi.repository.BookingRepository;
import com.nailpos.nailposapi.repository.CustomerRepository;
import com.nailpos.nailposapi.repository.StaffRepository;
import com.nailpos.nailposapi.service.IBookingService;
import com.nailpos.nailposapi.specification.BookingSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements IBookingService {
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Booking> getBookings(LocalDate startDate, LocalDate endDate, Long staffId, Pageable pageable) {
        Specification<Booking> spec = BookingSpecification.filterBy(startDate, endDate, staffId);
        return bookingRepository.findAll(spec,pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Booking createBooking(BookingRequestDTO dto) {
        if (dto.getEndTime().isBefore(dto.getStartTime())|| dto.getEndTime().isEqual(dto.getStartTime())){
            throw new InvalidDataException("Thời gian kết thúc phải sau thời gian bắt đầu.");
        }
        List<Booking> overlaps = bookingRepository.findOverlappingBookings(dto.getStaffId(), dto.getStartTime(), dto.getEndTime());
        if (!overlaps.isEmpty()){
            throw new InvalidDataException("Xung đột lịch! Nhân viên đã bận vào khung giờ này.");
        }

        Staff staff = staffRepository.findById(dto.getStaffId()).orElseThrow(()-> new ResourceNotFoundException("Staff", "id", dto.getStaffId()));
        Customer customer = null;
        if(dto.getCustomerId() != null){
            customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(()->new ResourceNotFoundException("Customer", "id", dto.getCustomerId()));
        }

        Booking newBooking = new Booking();
        newBooking.setCustomer(customer);
        newBooking.setStaff(staff);
        newBooking.setStartTime(dto.getStartTime());
        newBooking.setEndTime(dto.getEndTime());
        newBooking.setNotes(dto.getNotes());
        newBooking.setStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(newBooking);
    }

    @Override
    @Transactional
    public Booking updateBookingStatus(Long id, UpdateBookingStatusDTO statusDTO) {
        Booking booking = getBookingById(id);
        if (booking.getStatus()==BookingStatus.CANCELLED){
            throw  new InvalidDataException("Không thể cập nhật trạng thái của lịch đã bị hủy.");
        }
        booking.setStatus(statusDTO.getStatus());
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = getBookingById(id);
        bookingRepository.delete(booking);

    }
}
