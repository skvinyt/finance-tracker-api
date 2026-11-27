package com.example.finance_tracker.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.finance_tracker.dto.TransferRequest;
import com.example.finance_tracker.entity.Account;
import com.example.finance_tracker.entity.Transaction;
import com.example.finance_tracker.entity.TransactionType;
import com.example.finance_tracker.event.TransferCompletedEvent;
import com.example.finance_tracker.repository.AccountRepository;
import com.example.finance_tracker.repository.TransactionRepository;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TransferService(AccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           ApplicationEventPublisher eventPublisher) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void transfer(TransferRequest request) {
        // 1. Проверка суммы
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть больше нуля");
        }

        // 2. Защита от deadlock: блокируем счета в порядке возрастания ID
        Long firstId = Math.min(request.getFromAccountId(), request.getToAccountId());
        Long secondId = Math.max(request.getFromAccountId(), request.getToAccountId());

        Account firstLocked = accountRepository.findByIdWithLock(firstId)
                .orElseThrow(() -> new RuntimeException("Счет не найден: " + firstId));
        Account secondLocked = accountRepository.findByIdWithLock(secondId)
                .orElseThrow(() -> new RuntimeException("Счет не найден: " + secondId));

        Account fromAccount = firstLocked.getId().equals(request.getFromAccountId()) ? firstLocked : secondLocked;
        Account toAccount = firstLocked.getId().equals(request.getToAccountId()) ? firstLocked : secondLocked;

        // 3. Проверка баланса
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Недостаточно средств на счете " + fromAccount.getId());
        }

        // 4. Обновление баланса
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        // 5. Сохранение счетов
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // 6. Запись транзакции расхода (для отправителя)
        Transaction expense = new Transaction();
        expense.setAccount(fromAccount);
        expense.setAmount(request.getAmount());
        expense.setType(TransactionType.EXPENSE);
        expense.setCategory("Transfer");
        expense.setDescription("Перевод на счет " + toAccount.getId());
        expense.setCreatedAt(OffsetDateTime.now());
        transactionRepository.save(expense);

        // 7. Запись транзакции дохода (для получателя)
        Transaction income = new Transaction();
        income.setAccount(toAccount);
        income.setAmount(request.getAmount());
        income.setType(TransactionType.INCOME);
        income.setCategory("Transfer");
        income.setDescription("Перевод со счета " + fromAccount.getId());
        income.setCreatedAt(OffsetDateTime.now());
        transactionRepository.save(income);

        // 8. Публикация события для асинхронной обработки
        // (Слушатель получит это событие и выполнит свою работу в фоне)
        eventPublisher.publishEvent(new TransferCompletedEvent(
                fromAccount.getId(),
                toAccount.getId(),
                request.getAmount()
        ));
    }
}