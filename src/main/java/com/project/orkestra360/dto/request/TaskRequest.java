package com.project.orkestra360.dto.request;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.project.orkestra360.domain.enums.TaskPriority;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO representing the request payload to create a new task within a tenant.
 *
 * <p>{@code priority} defaults to {@code MEDIUM} at the domain layer when omitted.
 * {@code dueDate} and {@code assignedUserId} are optional.</p>
 */
public record TaskRequest(

        @NotBlank(message = "Task title is required")
        @Size(max = 255, message = "Task title must be 255 characters or less")
        String title,

        @Size(max = 1000, message = "Task description must be 1000 characters or less")
        String description,

        TaskPriority priority,

        @FutureOrPresent(message = "Due date must be in the present or future")
        OffsetDateTime dueDate,

        UUID assignedUserId

) {
}
