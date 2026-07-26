package org.example.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ValidationUtilsTest {

    @Test
    @DisplayName("Should validate correct ID formats")
    void testValidIds() {
        assertTrue(ValidationUtils.isValidId("B000001"));
        assertTrue(ValidationUtils.isValidId("M001"));
        assertFalse(ValidationUtils.isValidId("123"));
        assertFalse(ValidationUtils.isValidId("ABC"));
        assertFalse(ValidationUtils.isValidId(null));
        assertFalse(ValidationUtils.isValidId(""));
    }

    @Test
    @DisplayName("Should validate email addresses")
    void testValidEmails() {
        assertTrue(ValidationUtils.isValidEmail("user@example.com"));
        assertTrue(ValidationUtils.isValidEmail("user.name@domain.co.uk"));
        assertFalse(ValidationUtils.isValidEmail("invalid-email"));
        assertFalse(ValidationUtils.isValidEmail(null));
    }

    @Test
    @DisplayName("Should validate text content")
    void testHasText() {
        assertTrue(ValidationUtils.hasText("valid text"));
        assertFalse(ValidationUtils.hasText(""));
        assertFalse(ValidationUtils.hasText("   "));
        assertFalse(ValidationUtils.hasText(null));
    }

    @Test
    @DisplayName("Should validate positive numbers")
    void testIsPositive() {
        assertTrue(ValidationUtils.isPositive(1));
        assertTrue(ValidationUtils.isPositive(100));
        assertFalse(ValidationUtils.isPositive(0));
        assertFalse(ValidationUtils.isPositive(-1));
    }
}
