# Finance Tracker API (Core Service)

Основной микросервис системы учёта личных финансов. Отвечает за бизнес-логику, работу с базой данных, переводы средств и генерацию отчётов.

## 🛠 Стек технологий
* **Java 21**, **Spring Boot 4.x**
* **PostgreSQL**, **Flyway** (миграции)
* **Spring Data JPA**, **Hibernate**
* **Resilience4j** (Circuit Breaker для отказоустойчивости)
* **JUnit 5, Mockito, MockMvc** (тестирование)

## ⚙️ Ключевой функционал
* Управление счетами и транзакциями (CRUD).
* **Перевод средств** между счетами с защитой от race condition (`SELECT FOR UPDATE`) и deadlock.
* **Генерация отчётов** (Паттерн **Strategy**): расходы по категориям, баланс по месяцам, топ-3 крупнейших трат (Алгоритм на **PriorityQueue**).
* **Асинхронные уведомления** (Spring Events + `@Async`): после перевода событие публикуется в фоновый поток.
* **Микросервисное взаимодействие**: `NotificationClient` отправляет HTTP-запрос в `notification-service`.
* **Отказоустойчивость**: `Circuit Breaker` (Resilience4j) перехватывает ошибки, если сервис уведомлений недоступен, и вызывает fallback-метод. Перевод при этом не падает.

## 🔗 Связанные репозитории
* [Notification Service (Микросервис уведомлений)](https://github.com/skvinyt/notification-service)

## 🚀 Запуск
1. Создайте БД `finance_tracker` в PostgreSQL.
2. Настройте `application.properties` (пароль к БД).
3. Запустите: `./gradlew bootRun` (Сервис поднимется на порту 8080).
4. Для проверки уведомлений запустите также [notification-service](https://github.com/skvinyt/notification-service) на порту 8081.