package com.example.dbstub.controller;

import com.example.dbstub.dto.TextRequest;
import com.example.dbstub.dto.TextResponse;
import com.example.dbstub.service.TextProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/text")
@RequiredArgsConstructor
@Slf4j
public class TextController {

    private final TextProcessingService textProcessingService;

    @PostMapping("/process")
    public ResponseEntity<TextResponse> processText(@RequestBody TextRequest request) {
        log.info("Received text: {}", request.getText());

        TextResponse response = textProcessingService.processText(request.getText());

        return ResponseEntity.ok(response);
    }
}
