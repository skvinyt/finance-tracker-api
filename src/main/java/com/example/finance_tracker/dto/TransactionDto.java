package com.example.finance_tracker.dto;

import com.example.finance_tracker.entity.Transaction;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TransactionDto {
    private Long id;
    private BigDecimal amount;
    private String type;
    private String category;
    private String description;
    private OffsetDateTime createdAt;
    private Long accountId; // Вместо целого объекта Account отдаем только ID

    public TransactionDto(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.type = transaction.getType().name();
        this.category = transaction.getCategory();
        this.description = transaction.getDescription();
        this.createdAt = transaction.getCreatedAt();
        // Безопасно получаем ID счета, если он есть
        this.accountId = transaction.getAccount() != null ? transaction.getAccount().getId() : null;
    }

    // Геттеры
    public Long getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public String getType() { return type; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public Long getAccountId() { return accountId; }
}