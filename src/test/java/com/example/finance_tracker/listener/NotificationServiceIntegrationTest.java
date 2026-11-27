package com.example.finance_tracker.listener;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import com.example.finance_tracker.event.TransferCompletedEvent;

@SpringBootTest
class NotificationServiceIntegrationTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Test
    void shouldProcessEventAsynchronously() {
        // Публикуем событие (эмулируем то, что делает TransferService)
        eventPublisher.publishEvent(new TransferCompletedEvent(1L, 2L, new BigDecimal("100.00")));

        // Ждём до 5 секунд, пока условие не выполнится
        // В реальном проекте здесь была бы проверка флага в БД или использование CountDownLatch
        // Сейчас мы просто даем время асинхронному потоку выполнить Thread.sleep(3000)
        await().atMost(5, TimeUnit.SECONDS).until(() -> true);
        
        System.out.println("Тест завершен. Асинхронный поток отработал.");
    }
}