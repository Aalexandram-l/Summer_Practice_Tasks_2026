package com.example.dbstub.integration;

import com.example.dbstub.dto.KafkaMessage;
import com.example.dbstub.kafka.KafkaProducer;
import com.example.dbstub.repository.RequestRepository;
import com.example.dbstub.repository.ResponseRepository;
import com.example.dbstub.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LoadTest {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Test
    void testLoadWith100Messages() throws Exception {
        int messageCount = 100;
        System.out.println("=== Нагрузочный тест: " + messageCount + " сообщений ===");

        Instant start = Instant.now();

        for (int i = 0; i < messageCount; i++) {
            KafkaMessage message = new KafkaMessage();
            message.setId("load-test-" + i + "-" + System.currentTimeMillis());
            message.setText("Load test message #" + i);
            kafkaProducer.sendSuccess(message);
        }

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);

        System.out.println("Отправлено сообщений: " + messageCount);
        System.out.println("Время отправки: " + duration.toMillis() + " ms");
        System.out.println("Среднее время на сообщение: " + (duration.toMillis() / messageCount) + " ms");

        long taskCount = taskRepository.count();
        System.out.println("Записей в task: " + taskCount);
    }

    @Test
    void testLoadWith1000Messages() throws Exception {
        int messageCount = 1000;
        System.out.println("=== Нагрузочный тест: " + messageCount + " сообщений ===");

        Instant start = Instant.now();

        for (int i = 0; i < messageCount; i++) {
            KafkaMessage message = new KafkaMessage();
            message.setId("load-test-" + i + "-" + System.currentTimeMillis());
            message.setText("Load test message #" + i);
            kafkaProducer.sendSuccess(message);

            if (i % 100 == 0) {
                System.out.println("Отправлено: " + i + " сообщений");
            }
        }

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);

        System.out.println("Отправлено сообщений: " + messageCount);
        System.out.println("Время отправки: " + duration.toMillis() + " ms");
        System.out.println("Среднее время на сообщение: " + (duration.toMillis() / messageCount) + " ms");
        System.out.println("Сообщений в секунду: " + (messageCount * 1000 / duration.toMillis()));
    }

    @Test
    void testConcurrentLoad() throws Exception {
        int threads = 10;
        int messagesPerThread = 50;
        int totalMessages = threads * messagesPerThread;

        System.out.println("=== Конкурентный нагрузочный тест ===");
        System.out.println("Потоков: " + threads);
        System.out.println("Сообщений на поток: " + messagesPerThread);
        System.out.println("Всего сообщений: " + totalMessages);

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        Instant start = Instant.now();

        for (int t = 0; t < threads; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < messagesPerThread; i++) {
                        try {
                            KafkaMessage message = new KafkaMessage();
                            message.setId("concurrent-" + threadId + "-" + i + "-" + System.currentTimeMillis());
                            message.setText("Concurrent test from thread " + threadId + " message " + i);
                            kafkaProducer.sendSuccess(message);
                            successCount.incrementAndGet();
                        } catch (Exception e) {
                            errorCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);

        System.out.println("\n=== Результаты ===");
        System.out.println("Успешно отправлено: " + successCount.get());
        System.out.println("Ошибок: " + errorCount.get());
        System.out.println("Общее время: " + duration.toMillis() + " ms");
        System.out.println("Сообщений в секунду: " + (totalMessages * 1000 / duration.toMillis()));

        assertThat(errorCount.get()).isEqualTo(0);
    }

    @Test
    void testDatabaseLoad() {
        int operations = 100;
        System.out.println("=== Нагрузочный тест БД: " + operations + " операций ===");

        Instant start = Instant.now();

        for (int i = 0; i < operations; i++) {
            taskRepository.findAll();
            requestRepository.findAll();
            responseRepository.findAll();
        }

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);

        System.out.println("Выполнено операций чтения: " + operations * 3);
        System.out.println("Время выполнения: " + duration.toMillis() + " ms");
        System.out.println("Операций в секунду: " + (operations * 3 * 1000 / duration.toMillis()));
    }
}
