package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.TransactionDto;
import com.example.finance_tracker.entity.Transaction;
import com.example.finance_tracker.entity.TransactionType;
import com.example.finance_tracker.repository.TransactionRepository;
import com.example.finance_tracker.strategy.ReportStrategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final Map<String, ReportStrategy> strategies;

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

        List<Transaction> transactions = transactionRepository.findAll();
        return strategy.generateReport(transactions);
    }

    public List<TransactionDto> findTop3Expenses() {
        List<Transaction> allTransactions = transactionRepository.findAll();

        PriorityQueue<Transaction> topExpenses = new PriorityQueue<>(
                Comparator.comparing(Transaction::getAmount)
        );

        for (Transaction t : allTransactions) {
            if (t.getType() == TransactionType.EXPENSE) {
                topExpenses.offer(t);
                if (topExpenses.size() > 3) {
                    topExpenses.poll();
                }
            }
        }

        List<Transaction> result = new ArrayList<>(topExpenses);
        result.sort(Comparator.comparing(Transaction::getAmount).reversed());

        // Преобразуем в DTO для безопасной отправки клиенту
        return result.stream()
                .map(TransactionDto::new)
                .collect(Collectors.toList());
    }
}