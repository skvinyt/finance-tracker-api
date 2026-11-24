package com.example.finance_tracker.controller;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.example.finance_tracker.entity.Account;
import com.example.finance_tracker.repository.AccountRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Откатывает изменения в БД после каждого теста
class TransferControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void transfer_shouldReturnOkAndChangeBalances() throws Exception {
        // 1. Принудительно сбрасываем балансы к исходным значениям (независимость от предыдущих запусков)
        Account acc1 = accountRepository.findById(1L).orElseThrow();
        acc1.setBalance(new BigDecimal("50000.00"));
        accountRepository.save(acc1);

        Account acc2 = accountRepository.findById(2L).orElseThrow();
        acc2.setBalance(new BigDecimal("150000.00"));
        accountRepository.save(acc2);

        // 2. Формируем JSON для запроса
        String jsonBody = """
                {
                  "fromAccountId": 1,
                  "toAccountId": 2,
                  "amount": 1000.00
                }
                """;

        // 3. Выполняем POST-запрос
        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Перевод выполнен успешно"));

        // 4. Проверяем, что балансы в базе действительно изменились
        // (так как тест @Transactional, после его окончания изменения откатятся)
        Account updatedAcc1 = accountRepository.findById(1L).orElseThrow();
        Account updatedAcc2 = accountRepository.findById(2L).orElseThrow();

        // Ожидаем: 50000 - 1000 = 49000, 150000 + 1000 = 151000
        assertThat(updatedAcc1.getBalance()).isEqualByComparingTo("49000.00");
        assertThat(updatedAcc2.getBalance()).isEqualByComparingTo("151000.00");
    }
}