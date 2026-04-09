package com.project.orkestra360.domain.events;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.project.orkestra360.domain.enums.TaskStatus;

/**
 * Domain event published when a task changes its workflow status.
 * 
 * This event captures the change in task status, including the tenant and task
 * identifiers, the previous and current status of the task, and the timestamp
 * of when the status change occurred. It allows other parts of the system to
 * react to changes in task status, such as sending notifications, updating
 * related data, or triggering workflows based on status transitions.
 */
public record TaskStatusChangedEvent(
        UUID tenantId,
        UUID taskId,
        TaskStatus previousStatus,
        TaskStatus currentStatus,
        OffsetDateTime occurredAt) implements DomainEvent {

    public TaskStatusChangedEvent(UUID tenantId, UUID taskId, TaskStatus previousStatus, TaskStatus currentStatus) {
        this(tenantId, taskId, previousStatus, currentStatus, OffsetDateTime.now());
    }
}