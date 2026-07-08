package com.example.dbstub.service;

import com.example.dbstub.dto.TextResponse;
import com.example.dbstub.entity.Request;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TextProcessingService {

    private final DatabaseService databaseService;
    private final AiService aiService;

    public TextResponse processText(String text) {
        log.info("Processing text via REST: {}", text);

        Request savedRequest = databaseService.saveText(text);
        log.info("Saved request with id: {}", savedRequest.getId());

        String aiResponse = aiService.processWithAI(text);
        log.info("AI response: {}", aiResponse);

        databaseService.updateResponse(savedRequest.getId(), aiResponse);
        log.info("AI response saved to DB for request {}", savedRequest.getId());

        return new TextResponse(
            savedRequest.getId(),
            savedRequest.getTask().getQuestion(),
            aiResponse,
            "COMPLETED"
        );
    }

    public String processRawText(String text) {
        log.info("Processing text via Kafka: {}", text);

        Request savedRequest = databaseService.saveText(text);
        log.info("Saved request with id: {}", savedRequest.getId());

        String aiResponse = aiService.processWithAI(text);
        log.info("AI response for Kafka: {}", aiResponse);

        databaseService.updateResponse(savedRequest.getId(), aiResponse);
        log.info("AI response saved to DB for request {}", savedRequest.getId());

        return aiResponse;
    }
}
