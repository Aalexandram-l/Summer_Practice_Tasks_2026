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

    private String lastAiResponse = "";

    public String processWithAI(String text) {
        log.info("Processing text: {}", text);

        Map<String, Integer> keywords = keywordExtractorService.extractKeywords(text);

        String response;

        if (!keywords.isEmpty()) {
            log.info("Keywords found: {}", keywords);
            String prompt = "Based on these keywords: " + keywords + ", generate a detailed response in Russian.";
            response = yandexGptClient.sendRequest(prompt);
            this.lastAiResponse = response;
            log.info("AI response: {}", response);
            return response;
        }

        log.info("No keywords found, sending full text");
        response = yandexGptClient.sendRequest(text);
        this.lastAiResponse = response;
        log.info("AI response: {}", response);
        return response;
    }

    public String getLastAiResponse() {
        return lastAiResponse;
    }
}
