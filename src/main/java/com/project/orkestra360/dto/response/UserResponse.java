package com.project.orkestra360.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.project.orkestra360.domain.model.User;

/**
 * DTO representing the API response payload for User-related endpoints.
 *
 * <p>Exposes only the fields that are safe and relevant for the client,
 * keeping infrastructure details (JPA version, soft-delete flag) opaque.</p>
 */
public record UserResponse(

        UUID id,
        UUID tenantId,
        String name,
        String email,
        String phone,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt

) {

    /**
     * Static factory that maps a {@link User} domain entity to this response record.
     *
     * @param user the domain entity to map
     * @return a populated {@link UserResponse}
     */
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getTenantId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
