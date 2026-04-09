package com.project.orkestra360.domain.value;

import com.project.orkestra360.exception.BusinessException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing a validated phone number.
 */
public final class PhoneNumber {

    // Simple regex pattern for basic phone number validation.
    // This can be enhanced to cover more complex cases if needed.
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?\\d[\\d\s-]{6,19}$");

    private final String value;

    private PhoneNumber(String value) {
        this.value = value;
    }

    /**
     * Factory method to create a PhoneNumber value object with validation.
     * 
     * @param phone the raw phone number string to validate and normalize
     * @return a validated PhoneNumber value object
     * @throws BusinessException if the phone number is null, blank, or does not
     *                           match the expected format
     */
    public static PhoneNumber of(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new BusinessException("Phone number cannot be blank");
        }
        String normalized = phone.trim();
        if (!PHONE_PATTERN.matcher(normalized).matches()) {
            throw new BusinessException("Invalid phone number format");
        }
        return new PhoneNumber(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PhoneNumber)) {
            return false;
        }
        PhoneNumber that = (PhoneNumber) o;
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