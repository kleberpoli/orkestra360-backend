package com.project.orkestra360.domain.events;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Domain event published when a new user is created.
 * 
 * This event captures the essential information about the newly created user,
 * including the tenant and user identifiers, email, and the timestamp of when
 * the user was created. It allows other parts of the system to react to the
 * creation of new users, such as sending notifications, initializing
 * user-specific data, or triggering related workflows.
 */
public record UserCreatedEvent(
        UUID tenantId,
        UUID userId,
        String email,
        OffsetDateTime occurredAt) implements DomainEvent {

    public UserCreatedEvent(UUID tenantId, UUID userId, String email) {
        this(tenantId, userId, email, OffsetDateTime.now());
    }
}