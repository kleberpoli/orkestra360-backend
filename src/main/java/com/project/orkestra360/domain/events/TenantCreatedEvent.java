package com.project.orkestra360.domain.events;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Domain event published when a new tenant is created.
 * 
 * This event captures the essential information about the newly created tenant,
 * including the tenant identifier, name, slug, and the timestamp of when the
 * tenant was created. It allows other parts of the system to react to the
 * creation of new tenants, such as sending notifications, initializing
 * tenant-specific data, or triggering related workflows.
 */
public record TenantCreatedEvent(
        UUID tenantId,
        String name,
        String slug,
        OffsetDateTime occurredAt) implements DomainEvent {

    public TenantCreatedEvent(UUID tenantId, String name, String slug) {
        this(tenantId, name, slug, OffsetDateTime.now());
    }
}