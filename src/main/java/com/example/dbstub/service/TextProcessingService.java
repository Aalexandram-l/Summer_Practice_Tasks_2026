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

    public TextResponse processText(String text) {
        log.info("Processing text: {}", text);

        Request savedRequest = databaseService.saveText(text);

        return new TextResponse(
            savedRequest.getId(),
            savedRequest.getTask().getQuestion(),
            savedRequest.getResponse().getAnswer(),
            "PENDING"
        );
    }
}
