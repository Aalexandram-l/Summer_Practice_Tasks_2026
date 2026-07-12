package com.example.dbstub.service;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KeywordExtractorServiceTest {

    private final KeywordExtractorService service = new KeywordExtractorService();

    @Test
    void testIsValid_Valid() {
        assertTrue(service.isValid("Тестовый текст"));
    }

    @Test
    void testIsValid_Null() {
        assertFalse(service.isValid(null));
    }

    @Test
    void testIsValid_Empty() {
        assertFalse(service.isValid(""));
    }

    @Test
    void testIsValid_DigitsOnly() {
        assertFalse(service.isValid("123"));
    }

    @Test
    void testIsValid_TooShort() {
        assertFalse(service.isValid("аб"));
    }

    @Test
    void testExtractKeywords_Valid() {
        String text = "Привет мир мир мир Java";
        Map<String, Integer> result = service.extractKeywords(text);

        assertNotNull(result);
        assertTrue(result.containsKey("мир"));
        assertEquals(3, result.get("мир"));
    }

    @Test
    void testExtractKeywords_Null() {
        Map<String, Integer> result = service.extractKeywords(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractKeywords_Empty() {
        Map<String, Integer> result = service.extractKeywords("");
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractKeywords_StopWordsOnly() {
        String text = "и в на с к у";
        Map<String, Integer> result = service.extractKeywords(text);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractKeywords_Limit() {
        String text = "один два три четыре пять шесть семь";
        Map<String, Integer> result = service.extractKeywords(text);
        assertEquals(5, result.size());
    }

    @Test
    void testExtractKeywords_CaseInsensitive() {
        String text = "Java java JAVa";
        Map<String, Integer> result = service.extractKeywords(text);
        assertEquals(3, result.get("java"));
    }
}
