package com.example.finance_tracker.strategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.finance_tracker.entity.Transaction;
import com.example.finance_tracker.entity.TransactionType;

@Component
public class CategoryReportStrategy implements ReportStrategy {

    @Override
    public Map<String, Object> generateReport(List<Transaction> transactions) {
        // Группируем только расходы (EXPENSE) по категориям и суммируем
        Map<String, BigDecimal> expensesByCategory = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory() != null ? t.getCategory() : "Без категории",
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        Map<String, Object> report = new HashMap<>();
        report.put("type", getReportType());
        report.put("data", expensesByCategory);
        return report;
    }

    @Override
    public String getReportType() {
        return "CATEGORY_EXPENSES";
    }
}