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

    @Value("${yandex.gpt.api.key}")
    private String apiKey;

    @Value("${yandex.gpt.folder.id}")
    private String folderId;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public YandexGptClient() {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    public String sendRequest(String text) {
        log.info("Sending request to Yandex GPT: {}", text);

        if (apiKey.isEmpty() || apiKey.equals("your-api-key-here")) {
            log.warn("Yandex GPT API key not found, using mock");
            return "Mock response for: " + text;
        }

        String url = "https://llm.api.cloud.yandex.net/foundationModels/v1/completion";

        String requestBody = String.format("""
                {
                    "modelUri": "gpt://%s/yandexgpt-lite",
                    "completionOptions": {
                        "temperature": 0.7,
                        "maxTokens": 500
                    },
                    "messages": [
                        {"role": "user", "text": "%s"}
                    ]
                }
                """, folderId, text);

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
                    .path("result")
                    .path("alternatives")
                    .path(0)
                    .path("message")
                    .path("text")
                    .asText();

            log.info("AI message: {}", aiMessage);
            return aiMessage;

        } catch (Exception e) {
            log.error("Error calling Yandex GPT: {}", e.getMessage());
            return "Error: " + e.getMessage();
        }
    }
}
