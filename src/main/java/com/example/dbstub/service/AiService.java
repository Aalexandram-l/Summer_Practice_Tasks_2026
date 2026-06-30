package com.example.dbstub.service;

import com.example.dbstub.client.YandexGptClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final YandexGptClient yandexGptClient;
    private final KeywordExtractorService keywordExtractorService;
    private final DatabaseService databaseService;

    private String lastAiResponse = "";

    public String processWithAI(String text) {
        log.info("Processing text: {}", text);

        Map<String, Integer> keywords = keywordExtractorService.extractKeywords(text);

        if (!keywords.isEmpty()) {
            log.info("Keywords found: {}", keywords);
            String prompt = "Based on these keywords: " + keywords + ", generate a detailed response in Russian.";
            String response = yandexGptClient.sendRequest(prompt);
            this.lastAiResponse = response;
            log.info("AI response: {}", response);
            return response;
        }

        log.warn("No keywords found for text: {}", text);

        String errorMessage = "No keywords found in request: " + text;
        databaseService.saveError(text, errorMessage);

        this.lastAiResponse = "Error: " + errorMessage;
        log.error("No {}", errorMessage);

        return "Error: " + errorMessage;
    }

    public String getLastAiResponse() {
        return lastAiResponse;
    }
}
