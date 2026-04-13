package com.project.orkestra360.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO representing the request payload to create a new tenant.
 */
public record TenantRequest(

        @NotBlank(message = "Code is required")
        String code,

        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 50, message = "Tenant name must be between 3 and 50 characters")
        String name,

        @NotBlank(message = "Tenant slug is required")
        @Size(max = 255, message = "Tenant slug must be 255 characters or less")
        String slug

) {
}