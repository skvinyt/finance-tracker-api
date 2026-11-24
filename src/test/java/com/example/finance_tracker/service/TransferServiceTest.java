package com.example.finance_tracker.service;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.finance_tracker.dto.TransferRequest;
import com.example.finance_tracker.entity.Account;
import com.example.finance_tracker.entity.Transaction;
import com.example.finance_tracker.repository.AccountRepository;
import com.example.finance_tracker.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransferService transferService;

    @Test
    void transfer_success() {
        // Подготовка
        Account from = new Account();
        from.setId(1L);
        from.setBalance(new BigDecimal("5000"));

        Account to = new Account();
        to.setId(2L);
        to.setBalance(new BigDecimal("1000"));

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountId(2L);
        request.setAmount(new BigDecimal("1000"));

        // Настраиваем моки: при вызове findByIdWithLock возвращаем наши счета
        when(accountRepository.findByIdWithLock(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findByIdWithLock(2L)).thenReturn(Optional.of(to));

        // Вызов
        transferService.transfer(request);

        // Проверки
        assertThat(from.getBalance()).isEqualTo(new BigDecimal("4000"));
        assertThat(to.getBalance()).isEqualTo(new BigDecimal("2000"));
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void transfer_throwsWhenInsufficientFunds() {
        Account from = new Account();
        from.setId(1L);
        from.setBalance(new BigDecimal("500")); // Мало денег

        Account to = new Account();
        to.setId(2L);
        to.setBalance(new BigDecimal("1000"));

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountId(2L);
        request.setAmount(new BigDecimal("1000"));

        when(accountRepository.findByIdWithLock(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findByIdWithLock(2L)).thenReturn(Optional.of(to));

        assertThatThrownBy(() -> transferService.transfer(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Недостаточно средств");

        verify(accountRepository, never()).save(any(Account.class));
    }
}