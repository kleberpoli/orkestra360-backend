package com.project.orkestra360.dto.request;

import java.time.OffsetDateTime;

import com.project.orkestra360.domain.enums.TaskPriority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO representing the request payload to update a task's mutable details.
 *
 * <p>Covers {@code title}, {@code description}, {@code priority}, and {@code dueDate}.
 * Assignment changes are handled separately via the dedicated assign endpoint.</p>
 */
public record UpdateTaskRequest(

        @NotBlank(message = "Task title is required")
        @Size(max = 255, message = "Task title must be 255 characters or less")
        String title,

        @Size(max = 1000, message = "Task description must be 1000 characters or less")
        String description,

        TaskPriority priority,

        OffsetDateTime dueDate

) {
}
