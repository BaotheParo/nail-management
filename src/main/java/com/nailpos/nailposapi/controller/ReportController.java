package com.nailpos.nailposapi.controller;

import com.nailpos.nailposapi.dto.*;
import com.nailpos.nailposapi.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final IReportService reportService;

    /**
     * API: Dashboard KPI (Doanh thu, Chi phí, Lợi nhuận, Khách mới...)
     * GET /api/reports/kpi-summary?startDate=...&endDate=...
     */
    @GetMapping("/kpi-summary")
    public ResponseEntity<ApiResponse<KpiSummaryDTO>> getKpiSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        KpiSummaryDTO kpiData = reportService.getKpiSummary(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok(kpiData, "Lấy số liệu thống kê KPI thành công"));
    }

    /**
     * API: Báo cáo Đối soát & Công nợ nội bộ (Q4 & Q5)
     * GET /api/reports/reconciliation?startDate=...&endDate=...
     */
    @GetMapping("/reconciliation")
    public ResponseEntity<ApiResponse<ReconciliationReportDTO>> getReconciliationReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        ReconciliationReportDTO reportData = reportService.getReconciliationReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok(reportData, "Lấy báo cáo đối soát thành công"));
    }

    // API: Top Khách hàng chi tiêu
    // GET /api/reports/top-customers?startDate=...&endDate=...&limit=5
    @GetMapping("/top-customers")
    public ResponseEntity<ApiResponse<List<TopCustomerDTO>>> getTopCustomers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "5") int limit) {

        List<TopCustomerDTO> result = reportService.getTopCustomers(startDate, endDate, limit);
        return ResponseEntity.ok(ApiResponse.ok(result, "Lấy danh sách khách hàng VIP thành công"));
    }

    // API: Top Dịch vụ bán chạy
    // GET /api/reports/top-services?startDate=...&endDate=...&limit=5
    @GetMapping("/top-services")
    public ResponseEntity<ApiResponse<List<TopServiceDTO>>> getTopServices(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "5") int limit) {

        List<TopServiceDTO> result = reportService.getTopServices(startDate, endDate, limit);
        return ResponseEntity.ok(ApiResponse.ok(result, "Lấy danh sách dịch vụ hot thành công"));
    }
}