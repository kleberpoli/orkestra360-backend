package com.project.orkestra360.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

/**
 * DTO representing the request payload to assign a task to a user.
 *
 * <p>The {@code userId} must reference an active user that belongs to the same
 * tenant as the task; tenant-isolation is enforced at the service layer.</p>
 */
public record AssignTaskRequest(

        @NotNull(message = "User ID is required for task assignment")
        UUID userId

) {
}
