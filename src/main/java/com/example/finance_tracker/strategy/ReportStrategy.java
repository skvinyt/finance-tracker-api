package com.example.finance_tracker.strategy;

import java.util.List;
import java.util.Map;

import com.example.finance_tracker.entity.Transaction;

public interface ReportStrategy {
    // Метод принимает список транзакций и возвращает сгенерированный отчёт в виде Map
    Map<String, Object> generateReport(List<Transaction> transactions);
    
    // Название стратегии (для логирования или выбора)
    String getReportType();
}