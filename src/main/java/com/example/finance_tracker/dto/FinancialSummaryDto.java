package com.example.finance_tracker.dto;

import java.math.BigDecimal;

public class FinancialSummaryDto {
    private final String userName;
    private final BigDecimal totalIncome;
    private final BigDecimal totalExpense;
    private final BigDecimal netBalance;
    private final String currency;

    private FinancialSummaryDto(Builder builder) {
        this.userName = builder.userName;
        this.totalIncome = builder.totalIncome;
        this.totalExpense = builder.totalExpense;
        this.netBalance = builder.netBalance;
        this.currency = builder.currency;
    }

    public String getUserName() { return userName; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public BigDecimal getTotalExpense() { return totalExpense; }
    public BigDecimal getNetBalance() { return netBalance; }
    public String getCurrency() { return currency; }

    public static class Builder {
        private String userName;
        private BigDecimal totalIncome = BigDecimal.ZERO;
        private BigDecimal totalExpense = BigDecimal.ZERO;
        private BigDecimal netBalance = BigDecimal.ZERO;
        private String currency = "RUB";

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder totalIncome(BigDecimal totalIncome) {
            this.totalIncome = totalIncome;
            return this;
        }

        public Builder totalExpense(BigDecimal totalExpense) {
            this.totalExpense = totalExpense;
            return this;
        }

        public Builder netBalance(BigDecimal netBalance) {
            this.netBalance = netBalance;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public FinancialSummaryDto build() {
            return new FinancialSummaryDto(this);
        }
    }
}