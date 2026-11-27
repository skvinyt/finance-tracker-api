package com.example.finance_tracker.strategy;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.finance_tracker.entity.Transaction;

@Component
public class MonthlyReportStrategy implements ReportStrategy {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public Map<String, Object> generateReport(List<Transaction> transactions) {
        // Группируем по месяцам (yyyy-MM) и считаем доходы и расходы
        Map<String, Map<String, BigDecimal>> monthlyData = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCreatedAt().format(FORMATTER),
                        Collectors.groupingBy(
                                t -> t.getType().name(),
                                Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                        )
                ));

        Map<String, Object> report = new HashMap<>();
        report.put("type", getReportType());
        report.put("data", monthlyData);
        return report;
    }

    @Override
    public String getReportType() {
        return "MONTHLY_BALANCE";
    }
}