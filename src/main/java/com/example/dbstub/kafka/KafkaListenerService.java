package com.example.dbstub.kafka;

import com.example.dbstub.dto.KafkaMessage;
import com.example.dbstub.service.TextProcessingService;
import com.example.dbstub.validator.TextValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {

    private final TextProcessingService textProcessingService;
    private final TextValidator textValidator;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.poll-interval:5000}")
    private long pollInterval;

    @KafkaListener(
        topics = "${app.kafka.topic.input:text-input}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(String message) {
        log.info("Received message from Kafka: {}", message);

        try {
            KafkaMessage kafkaMessage = objectMapper.readValue(message, KafkaMessage.class);
            String text = kafkaMessage.getText();

            if (!textValidator.isValid(text)) {
                log.warn("Invalid text: {}", text);
                kafkaMessage.setStatus("ERROR");
                kafkaMessage.setError("Invalid text: " + text);
                kafkaProducer.sendError(kafkaMessage);
                return;
            }

            String response = textProcessingService.processRawText(text);
            kafkaMessage.setStatus("SUCCESS");
            kafkaMessage.setId(UUID.randomUUID().toString());

            kafkaProducer.sendSuccess(kafkaMessage);

        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", e.getMessage());

            KafkaMessage errorMessage = new KafkaMessage();
            errorMessage.setId(UUID.randomUUID().toString());
            errorMessage.setStatus("ERROR");
            errorMessage.setError("Processing failed: " + e.getMessage());

            kafkaProducer.sendError(errorMessage);
        }
    }
}
