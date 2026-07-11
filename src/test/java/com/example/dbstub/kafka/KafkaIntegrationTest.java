package com.example.dbstub.kafka;

import com.example.dbstub.dto.KafkaMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
class KafkaIntegrationTest {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    void testSendSuccess() {
        KafkaMessage message = new KafkaMessage();
        message.setId("test-id-" + System.currentTimeMillis());
        message.setText("Test success message");
        
        assertThatCode(() -> kafkaProducer.sendSuccess(message))
            .doesNotThrowAnyException();
    }

    @Test
    void testSendError() {
        KafkaMessage message = new KafkaMessage();
        message.setId("test-id-" + System.currentTimeMillis());
        message.setText("Test error message");
        
        assertThatCode(() -> kafkaProducer.sendError(message))
            .doesNotThrowAnyException();
    }
}
