package com.example.dbstub.integration;

import com.example.dbstub.dto.KafkaMessage;
import com.example.dbstub.kafka.KafkaProducer;
import com.example.dbstub.repository.RequestRepository;
import com.example.dbstub.repository.ResponseRepository;
import com.example.dbstub.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
class FullCycleIntegrationTest {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Test
    void testFullCycle() {
        KafkaMessage message = new KafkaMessage();
        message.setId("test-full-cycle-" + System.currentTimeMillis());
        message.setText("Integration full cycle test");

        assertThatCode(() -> kafkaProducer.sendSuccess(message))
            .doesNotThrowAnyException();

        assertThatCode(() -> {
            taskRepository.findAll();
            requestRepository.findAll();
            responseRepository.findAll();
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            long count = taskRepository.count();
            System.out.println("Task count: " + count);
        }).doesNotThrowAnyException();
    }

    @Test
    void testSendError() {
        KafkaMessage message = new KafkaMessage();
        message.setId("test-error-" + System.currentTimeMillis());
        message.setText("Test error message");

        assertThatCode(() -> kafkaProducer.sendError(message))
            .doesNotThrowAnyException();
    }

    @Test
    void testDatabaseAccess() {
        assertThatCode(() -> {
            var tasks = taskRepository.findAll();
            var requests = requestRepository.findAll();
            var responses = responseRepository.findAll();
            
            System.out.println("Tasks: " + tasks.size());
            System.out.println("Requests: " + requests.size());
            System.out.println("Responses: " + responses.size());
        }).doesNotThrowAnyException();
    }
}
