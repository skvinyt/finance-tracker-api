package com.example.finance_tracker.client;

import com.example.finance_tracker.event.TransferCompletedEvent;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);
    private final RestClient restClient;

    public NotificationClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
    }

    @CircuitBreaker(name = "notificationService", fallbackMethod = "fallbackSendNotification")
    public void sendNotification(TransferCompletedEvent event) {
        log.info("Попытка отправить уведомление в notification-service...");
        restClient.post()
                .uri("/api/notifications")
                .body(event)
                .retrieve()
                .toBodilessEntity();
        log.info("HTTP-запрос в notification-service выполнен успешно");
    }

    // Этот метод будет вызван, если sendNotification выбросит исключение (сервис недоступен)
    private void fallbackSendNotification(TransferCompletedEvent event, Exception e) {
        log.error("⚠️ Notification-service недоступен! Уведомление не отправлено. Причина: {}", e.getMessage());
        // Здесь можно сохранить уведомление в БД, чтобы отправить его позже
        // Но для пет-проекта достаточно просто залогировать ошибку.
    }
}