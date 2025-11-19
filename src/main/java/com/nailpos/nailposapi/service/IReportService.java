package com.nailpos.nailposapi.service;

import com.nailpos.nailposapi.dto.KpiSummaryDTO;
import com.nailpos.nailposapi.dto.ReconciliationReportDTO;
import com.nailpos.nailposapi.dto.TopCustomerDTO;
import com.nailpos.nailposapi.dto.TopServiceDTO;

import java.time.LocalDate;
import java.util.List;

public interface IReportService {

    /**
     * Lấy các chỉ số KPI tổng quan (Dashboard)
     */
    KpiSummaryDTO getKpiSummary(LocalDate startDate, LocalDate endDate);

    /**
     * Tạo báo cáo đối soát công nợ nội bộ (Q4 & Q5)
     */
    ReconciliationReportDTO getReconciliationReport(LocalDate startDate, LocalDate endDate);

    List<TopCustomerDTO> getTopCustomers(LocalDate startDate, LocalDate endDate, int limit);

    List<TopServiceDTO> getTopServices(LocalDate startDate, LocalDate endDate, int limit);
}