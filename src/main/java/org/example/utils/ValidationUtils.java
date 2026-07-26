package org.example.utils;

import java.util.regex.Pattern;

/**
 * Utility class providing validation methods for various fields in the library
 * system.
 */
public class ValidationUtils {

    // ID format: letter followed by numbers (e.g., B000001, M001)
    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Z]\\d{3,6}$");

    // Email format validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    /**
     * Validates an ID string against the required format
     *
     * @param id The ID to validate
     * @return true if ID matches pattern, false otherwise
     */
    public static boolean isValidId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return ID_PATTERN.matcher(id).matches();
    }

    /**
     * Validates an email address
     *
     * @param email The email to validate
     * @return true if email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates that a string is not null or empty
     *
     * @param str The string to check
     * @return true if string has content, false otherwise
     */
    public static boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Validates that a number is positive
     *
     * @param num The number to check
     * @return true if number is positive, false otherwise
     */
    public static boolean isPositive(int num) {
        return num > 0;
    }
}
