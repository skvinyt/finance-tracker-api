package com.example.finance_tracker.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);      // Минимальное количество потоков
        executor.setMaxPoolSize(5);       // Максимальное количество потоков
        executor.setQueueCapacity(100);   // Очередь задач, если все потоки заняты
        executor.setThreadNamePrefix("Async-Notification-"); // Префикс для логов
        executor.initialize();
        return executor;
    }
}