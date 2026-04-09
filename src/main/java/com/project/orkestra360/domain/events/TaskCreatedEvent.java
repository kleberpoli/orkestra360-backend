package com.project.orkestra360.domain.events;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Domain event published when a new task is created in the system.
 * 
 * This event captures the essential information about the newly created task,
 * including the tenant and task identifiers, the task title, due date, and the
 * timestamp of when the task was created. It allows other parts of the system
 * to react to the creation of new tasks, such as sending notifications,
 * updating task lists, or triggering related workflows.
 */
public record TaskCreatedEvent(
        UUID tenantId,
        UUID taskId,
        String title,
        OffsetDateTime dueDate,
        OffsetDateTime occurredAt) implements DomainEvent {

    public TaskCreatedEvent(UUID tenantId, UUID taskId, String title, OffsetDateTime dueDate) {
        this(tenantId, taskId, title, dueDate, OffsetDateTime.now());
    }
}