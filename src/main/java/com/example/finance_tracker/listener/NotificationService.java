package com.example.finance_tracker.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.finance_tracker.event.TransferCompletedEvent;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Async("taskExecutor") // Выполнять в нашем кастомном пуле потоков
    @EventListener // Реагировать на событие TransferCompletedEvent
    public void handleTransferCompleted(TransferCompletedEvent event) {
        log.info("Начало отправки уведомления о переводе {} с счета {} на счет {}",
                event.getAmount(), event.getFromAccountId(), event.getToAccountId());

        try {
            // Эмулируем долгую работу (например, отправка email через SMTP)
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("Уведомление успешно отправлено! (Поток: {})", Thread.currentThread().getName());
    }
}