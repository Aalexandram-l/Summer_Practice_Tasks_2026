package com.example.dbstub.service;

import com.example.dbstub.client.YandexGptClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final YandexGptClient yandexGptClient;

    public String processWithAI(String text) {
        log.info("Sending text to Yandex GPT: {}", text);

        String response = yandexGptClient.sendRequest(text);

        log.info("AI response: {}", response);
        return response;
    }
}
