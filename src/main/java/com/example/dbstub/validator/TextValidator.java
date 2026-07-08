package com.example.dbstub.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TextValidator {

    private static final int MAX_TEXT_LENGTH = 10000;

    public boolean isValid(String text) {
        if (text == null || text.trim().isEmpty()) {
            log.warn("Validation failed: text is null or empty");
            return false;
        }

        if (text.length() > MAX_TEXT_LENGTH) {
            log.warn("Validation failed: text length {} exceeds max {}", text.length(), MAX_TEXT_LENGTH);
            return false;
        }

        log.debug("Validation passed for text: {}...", text.substring(0, Math.min(text.length(), 50)));
        return true;
    }
}
