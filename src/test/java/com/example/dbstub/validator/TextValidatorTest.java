package com.example.dbstub.validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextValidatorTest {

    private final TextValidator validator = new TextValidator();

    @Test
    void testIsValid_ValidText() {
        assertTrue(validator.isValid("Привет мир"));
    }

    @Test
    void testIsValid_NullText() {
        assertFalse(validator.isValid(null));
    }

    @Test
    void testIsValid_EmptyText() {
        assertFalse(validator.isValid(""));
        assertFalse(validator.isValid("   "));
    }

    @Test
    void testIsValid_ValidWithSpaces() {
        assertTrue(validator.isValid("  Текст с пробелами  "));
    }

    @Test
    void testIsValid_MaxLength() {
        String text = "a".repeat(10000);
        assertTrue(validator.isValid(text));
    }

    @Test
    void testIsValid_ExceedsMaxLength() {
        String text = "a".repeat(10001);
        assertFalse(validator.isValid(text));
    }

    @Test
    void testIsValid_SpecialCharacters() {
        assertTrue(validator.isValid("!@#$%^&*()"));
        assertTrue(validator.isValid("😊 Привет 🌍"));
    }

    @Test
    void testIsValid_ShortText() {
        assertTrue(validator.isValid("a"));
        assertTrue(validator.isValid("ab"));
        assertTrue(validator.isValid("abc"));
    }
}
