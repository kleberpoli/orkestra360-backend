package com.project.orkestra360.dto.request;

import com.project.orkestra360.domain.enums.TaskStatus;

import jakarta.validation.constraints.NotNull;

/**
 * DTO representing the request payload to update a task's status.
 *
 * <p>Transitions are validated against the state machine defined in
 * {@link com.project.orkestra360.domain.enums.TaskStatus#canTransitionTo}.</p>
 */
public record UpdateTaskStatusRequest(

        @NotNull(message = "Task status is required")
        TaskStatus status

) {
}
