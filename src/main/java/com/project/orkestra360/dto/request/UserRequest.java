package com.project.orkestra360.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO representing the request payload to create or update a user within a tenant.
 *
 * <p>Validated at the controller boundary via {@code @Valid}, so that malformed
 * input never reaches the service or domain layer.</p>
 */
public record UserRequest(

        @NotBlank(message = "User name is required")
        @Size(max = 255, message = "User name must be 255 characters or less")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        @Size(max = 255, message = "Email must be 255 characters or less")
        String email,

        @Size(max = 20, message = "Phone number must be 20 characters or less")
        String phone

) {
}
