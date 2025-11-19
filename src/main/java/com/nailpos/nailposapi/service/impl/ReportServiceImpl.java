package com.nailpos.nailposapi.service.impl;

import com.nailpos.nailposapi.dto.*;
import com.nailpos.nailposapi.exception.InvalidDataException;
import com.nailpos.nailposapi.model.Staff;
import com.nailpos.nailposapi.repository.*;
import com.nailpos.nailposapi.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements IReportService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRevenueRepository invoiceItemRevenueRepository;
    private final ExpenseRepository expenseRepository;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final InvoiceItemRepository invoiceItemRepository;

    @Override
    @Transactional(readOnly = true)
    public KpiSummaryDTO getKpiSummary(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);

        // Chuyển đổi LocalDate -> LocalDateTime để query chính xác
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // 1. Tổng doanh thu (sau giảm giá)
        BigDecimal totalRevenue = invoiceItemRevenueRepository.sumFinalRevenueByDateRange(startDateTime, endDateTime);

        // 2. Tổng chi phí
        BigDecimal totalExpenses = expenseRepository.sumAmountByDateRange(startDate, endDate);

        // 3. Lợi nhuận = Doanh thu - Chi phí
        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);

        // 4. Tổng số hóa đơn
        long totalInvoices = invoiceRepository.countByDateRange(startDateTime, endDateTime);

        // 5. Số lượng khách hàng mới
        long newCustomers = customerRepository.countNewCustomersByDateRange(startDateTime, endDateTime);

        return new KpiSummaryDTO(totalRevenue, totalExpenses, netProfit, totalInvoices, newCustomers);
    }

    @Override
    @Transactional(readOnly = true)
    public ReconciliationReportDTO getReconciliationReport(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);

        // Chuyển đổi thời gian
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // 1. Lấy danh sách nhân viên (Ánh, Sương...)
        List<Staff> staffList = staffRepository.findAll();
        if (staffList.isEmpty()) {
            throw new InvalidDataException("Không tìm thấy nhân viên nào để đối soát.");
        }

        // 2. Tính Tổng quan toàn tiệm
        BigDecimal totalRevenue = invoiceItemRevenueRepository.sumFinalRevenueByDateRange(startDateTime, endDateTime);
        BigDecimal totalExpenses = expenseRepository.sumAmountByDateRange(startDate, endDate);
        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);

        // 3. Tính Chi phí Phải chịu mỗi người (Q4)
        // Chia đều tổng chi phí cho số lượng nhân viên
        BigDecimal expenseSharePerStaff = (totalExpenses.compareTo(BigDecimal.ZERO) == 0)
                ? BigDecimal.ZERO
                : totalExpenses.divide(new BigDecimal(staffList.size()), 2, RoundingMode.HALF_UP);

        // 4. Vòng lặp tính chi tiết từng người (Q5)
        List<StaffReconciliationDetailDTO> details = new ArrayList<>();

        for (Staff staff : staffList) {
            // a. Doanh thu người này mang về
            BigDecimal revenueGenerated = invoiceItemRevenueRepository.sumFinalRevenueByStaffAndDateRange(
                    staff.getStaffId(), startDateTime, endDateTime);

            // b. Tiền người này ĐÃ CHI
            BigDecimal expensesPaid = expenseRepository.sumAmountByStaffAndDateRange(
                    staff.getStaffId(), startDate, endDate);

            // c. Tính Công nợ (Q5 - Cốt lõi)
            // finalBalance = Tiền đã chi - Tiền phải chi
            // Dương (+): Đã chi nhiều hơn trách nhiệm -> Tiệm nợ lại nhân viên.
            // Âm (-): Chi ít hơn trách nhiệm -> Phải nộp thêm tiền.
            BigDecimal finalBalance = expensesPaid.subtract(expenseSharePerStaff);

            details.add(new StaffReconciliationDetailDTO(
                    staff.getStaffId(),
                    staff.getName(),
                    revenueGenerated,
                    expensesPaid,
                    finalBalance
            ));
        }

        return new ReconciliationReportDTO(
                startDate,
                endDate,
                totalRevenue,
                totalExpenses,
                netProfit,
                expenseSharePerStaff,
                details
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopCustomerDTO> getTopCustomers(LocalDate startDate, LocalDate endDate, int limit) {
        validateDateRange(startDate, endDate);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // Dùng PageRequest để lấy giới hạn số lượng (Top N)
        return customerRepository.findTopSpendingCustomers(
                startDateTime, endDateTime, PageRequest.of(0, limit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopServiceDTO> getTopServices(LocalDate startDate, LocalDate endDate, int limit) {
        validateDateRange(startDate, endDate);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return invoiceItemRepository.findTopServices(
                startDateTime, endDateTime, PageRequest.of(0, limit));
    }

    // Hàm phụ trợ validate ngày
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidDataException("Ngày bắt đầu và kết thúc không được để trống.");
        }
        if (endDate.isBefore(startDate)) {
            throw new InvalidDataException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
        }
    }
}