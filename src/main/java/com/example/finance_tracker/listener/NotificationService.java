package com.example.finance_tracker.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.finance_tracker.client.NotificationClient;
import com.example.finance_tracker.event.TransferCompletedEvent;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationClient notificationClient;

    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    @Async("taskExecutor")
    @EventListener
    public void handleTransferCompleted(TransferCompletedEvent event) {
        log.info("Начало обработки уведомления о переводе {} с счета {} на счет {}",
                event.getAmount(), event.getFromAccountId(), event.getToAccountId());
        
        // Вызываем другой микросервис
        notificationClient.sendNotification(event);
    }
}