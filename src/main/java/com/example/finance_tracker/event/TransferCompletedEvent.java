package com.example.finance_tracker.event;

import java.math.BigDecimal;

public class TransferCompletedEvent {
    private final Long fromAccountId;
    private final Long toAccountId;
    private final BigDecimal amount;

    public TransferCompletedEvent(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}