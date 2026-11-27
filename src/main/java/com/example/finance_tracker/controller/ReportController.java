package com.example.finance_tracker.controller;

import com.example.finance_tracker.dto.TransactionDto;
import com.example.finance_tracker.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/{reportType}")
    public ResponseEntity<Map<String, Object>> getReport(@PathVariable String reportType) {
        Map<String, Object> report = reportService.generateReport(reportType);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/top-expenses")
    public ResponseEntity<List<TransactionDto>> getTopExpenses() {
        return ResponseEntity.ok(reportService.findTop3Expenses());
    }
}