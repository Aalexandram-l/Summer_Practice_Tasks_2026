package com.example.dbstub.service;

import com.example.dbstub.client.YandexGptClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private YandexGptClient yandexGptClient;

    @Mock
    private KeywordExtractorService keywordExtractorService;

    @Mock
    private DatabaseService databaseService;

    @InjectMocks
    private AiService aiService;

    @Test
    void testProcessWithAI_ValidText() {
        String text = "Тестовый текст";
        when(keywordExtractorService.isValid(text)).thenReturn(true);
        when(keywordExtractorService.extractKeywords(text)).thenReturn(Map.of("тест", 1));
        when(yandexGptClient.sendRequest(anyString())).thenReturn("Ответ AI");

        String result = aiService.processWithAI(text);

        assertNotNull(result);
        assertEquals("Ответ AI", result);
    }

    @Test
    void testProcessWithAI_InvalidText() {
        String text = "";
        when(keywordExtractorService.isValid(text)).thenReturn(false);

        String result = aiService.processWithAI(text);

        assertNotNull(result);
        assertTrue(result.startsWith("Error:"));
        verify(databaseService).saveError(eq(text), anyString());
    }

    @Test
    void testGetLastAiResponse() {
        String text = "Тестовый текст";
        when(keywordExtractorService.isValid(text)).thenReturn(true);
        when(keywordExtractorService.extractKeywords(text)).thenReturn(Map.of("тест", 1));
        when(yandexGptClient.sendRequest(anyString())).thenReturn("Ответ AI");

        aiService.processWithAI(text);
        String response = aiService.getLastAiResponse();

        assertEquals("Ответ AI", response);
    }
}
