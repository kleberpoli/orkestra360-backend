package com.project.orkestra360.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.project.orkestra360.domain.enums.TaskPriority;
import com.project.orkestra360.domain.enums.TaskStatus;
import com.project.orkestra360.domain.model.Task;

/**
 * DTO representing the API response payload for Task-related endpoints.
 *
 * <p>Encapsulates task details along with an optional summary of the assigned user,
 * keeping the client decoupled from the domain model internals.</p>
 *
 * <p>The {@code overdue} flag is computed at mapping time from the domain entity,
 * so clients do not need to replicate that logic.</p>
 */
public record TaskResponse(

        UUID id,
        UUID tenantId,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        OffsetDateTime dueDate,
        boolean overdue,
        AssignedUserSummary assignedTo,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt

) {

    /**
     * Minimal projection of the assigned user, embedded inside a {@link TaskResponse}.
     *
     * <p>Avoids a full {@link UserResponse} to prevent over-fetching;
     * clients that need full user details can call the User endpoint separately.</p>
     */
    public record AssignedUserSummary(UUID id, String name, String email) {

        /**
         * Maps a {@link com.project.orkestra360.domain.model.User} to this summary record.
         *
         * @param user the user entity; must not be {@code null}
         * @return a populated {@link AssignedUserSummary}
         */
        public static AssignedUserSummary fromEntity(com.project.orkestra360.domain.model.User user) {
            return new AssignedUserSummary(user.getId(), user.getName(), user.getEmail());
        }
    }

    /**
     * Static factory that maps a {@link Task} domain entity to this response record.
     *
     * @param task the domain entity to map
     * @return a fully populated {@link TaskResponse}
     */
    public static TaskResponse fromEntity(Task task) {
        AssignedUserSummary assignedUserSummary = task.getAssignedTo() != null
                ? AssignedUserSummary.fromEntity(task.getAssignedTo())
                : null;

        return new TaskResponse(
                task.getId(),
                task.getTenantId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.isOverdue(),
                assignedUserSummary,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
