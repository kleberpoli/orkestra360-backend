package com.project.orkestra360.domain.value;

import com.project.orkestra360.exception.BusinessException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object that represents a normalized email address.
 */
public final class EmailAddress {

    // Simple regex pattern for basic email validation.
    // This can be enhanced to cover more complex cases if needed.
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String value;

    private EmailAddress(String value) {
        this.value = value;
    }

    /**
     * Factory method to create an EmailAddress value object with validation and
     * normalization.
     * 
     * @param email the raw email address string to validate and normalize
     * @return a validated and normalized EmailAddress value object
     * @throws BusinessException if the email is null, blank, or does not match the
     *                           expected format
     */
    public static EmailAddress of(String email) {
        if (email == null || email.isBlank()) {
            throw new BusinessException("Email address is required");
        }
        String normalized = email.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new BusinessException("Invalid email address format");
        }
        return new EmailAddress(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmailAddress)) {
            return false;
        }
        EmailAddress that = (EmailAddress) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}