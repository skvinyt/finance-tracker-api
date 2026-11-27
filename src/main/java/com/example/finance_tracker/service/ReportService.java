package com.example.finance_tracker.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.finance_tracker.entity.Transaction;
import com.example.finance_tracker.repository.TransactionRepository;
import com.example.finance_tracker.strategy.ReportStrategy;

@Service
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final Map<String, ReportStrategy> strategies;

    // Spring автоматически внедрит все бины, реализующие ReportStrategy, в этот Map
    public ReportService(TransactionRepository transactionRepository, List<ReportStrategy> strategyList) {
        this.transactionRepository = transactionRepository;
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(ReportStrategy::getReportType, s -> s));
    }

    public Map<String, Object> generateReport(String reportType) {
        ReportStrategy strategy = strategies.get(reportType);
        if (strategy == null) {
            throw new IllegalArgumentException("Неизвестный тип отчёта: " + reportType);
        }

        // В реальном проекте здесь была бы фильтрация по userId или accountId
        List<Transaction> transactions = transactionRepository.findAll();
        return strategy.generateReport(transactions);
    }
}