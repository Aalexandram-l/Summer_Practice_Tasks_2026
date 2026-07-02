package com.example.dbstub.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class YandexGptClient {

    @Value("${yandex.gpt.api.key:}")
    private String apiKey;

    @Value("${yandex.gpt.folder.id:}")
    private String folderId;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public YandexGptClient() {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    public String sendRequest(String text) {
        log.info("Sending request to Yandex GPT: {}", text);

        if (apiKey == null || apiKey.isEmpty()) {
            String error = "ERROR: Yandex GPT API key is not configured. Please set YANDEX_GPT_API_KEY environment variable.";
            log.error("{}", error);
            return error;
        }

        if (folderId == null || folderId.isEmpty()) {
            String error = "ERROR: Yandex GPT folder ID is not configured. Please set YANDEX_GPT_FOLDER_ID environment variable.";
            log.error("{}", error);
            return error;
        }

        String url = "https://ai.api.cloud.yandex.net/v1/chat/completions";

        String model = "gpt://b1gta9m73hnooopmbqkt/yandexgpt-4-lite/latest";

        String escapedText = text.replace("\"", "\\\"");

        String requestBody = String.format("""
                {
                    "model": "%s",
                    "messages": [
                        {
                            "role": "user",
                            "content": "%s"
                        }
                    ],
                    "temperature": 0.6,
                    "max_tokens": 500
                }
                """, model, escapedText);

        log.info("URL: {}", url);
        log.info("Model: {}", model);
        log.info("Request body: {}", requestBody);

        try {
            String response = webClient.post()
                    .uri(url)
                    .header("Authorization", "Api-Key " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Yandex GPT response received");

            JsonNode root = objectMapper.readTree(response);
            String aiMessage = root
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText();

            log.info("AI message: {}", aiMessage);
            return aiMessage;

        } catch (Exception e) {
            log.error("Error calling Yandex GPT: {}", e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }
}
