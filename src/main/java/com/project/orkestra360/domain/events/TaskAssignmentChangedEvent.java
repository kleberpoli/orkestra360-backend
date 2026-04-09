package com.project.orkestra360.domain.events;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Domain event published when a task is assigned or unassigned from a user.
 * 
 * This event captures the change in task assignment, including the tenant and
 * task identifiers, the user to whom the task is assigned (or unassigned if
 * null), and the timestamp of when the assignment change occurred. It allows
 * other parts of the system to react to changes in task assignments, such as
 * sending notifications or updating related data.
 */
public record TaskAssignmentChangedEvent(
        UUID tenantId,
        UUID taskId,
        UUID assignedUserId,
        OffsetDateTime occurredAt) implements DomainEvent {

    public TaskAssignmentChangedEvent(UUID tenantId, UUID taskId, UUID assignedUserId) {
        this(tenantId, taskId, assignedUserId, OffsetDateTime.now());
    }
}