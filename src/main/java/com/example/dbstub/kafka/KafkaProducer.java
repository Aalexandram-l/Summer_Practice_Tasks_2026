package com.example.dbstub.kafka;

import com.example.dbstub.dto.KafkaMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topic.output:text-output}")
    private String outputTopic;

    @Value("${app.kafka.topic.error:text-error}")
    private String errorTopic;

    public void sendSuccess(KafkaMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(outputTopic, message.getId(), json);
            log.info("Success message sent to Kafka: {}", message.getId());
        } catch (Exception e) {
            log.error("Failed to send success message: {}", e.getMessage());
        }
    }

    public void sendError(KafkaMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(errorTopic, message.getId(), json);
            log.info("Error message sent to Kafka: {}", message.getId());
        } catch (Exception e) {
            log.error("Failed to send error message: {}", e.getMessage());
        }
    }
}
