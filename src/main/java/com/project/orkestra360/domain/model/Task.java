package com.project.orkestra360.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import com.project.orkestra360.domain.enums.TaskPriority;
import com.project.orkestra360.domain.enums.TaskStatus;
import com.project.orkestra360.exception.BusinessException;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Represents a task in the system.
 * Tasks are the fundamental units of work that can be assigned to users and
 * tracked through different statuses. The Task entity includes fields for
 * title, description, status, priority, due date, and assigned user. It also
 * implements soft deletion to allow for data recovery and auditing. Business
 * logic for task completion and state validation is included to ensure proper
 * workflow management.
 */
@Entity
@Table(name = "tasks")
@SQLDelete(sql = "UPDATE tasks SET active = false WHERE id = ?")
@SQLRestriction("active = true")
public class Task extends BaseEntity {

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String title;

    @Size(max = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;

    private OffsetDateTime dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id", referencedColumnName = "id")
    private User assignedTo;

    protected Task() {}

    private Task(UUID tenantId, String title, String description, TaskPriority priority, OffsetDateTime dueDate, User assignedTo) {
        super(tenantId);
        this.title = title;
        this.description = description;
        this.priority = (priority != null) ? priority : TaskPriority.MEDIUM;
        this.dueDate = dueDate;
        if (assignedTo != null && !assignedTo.getTenantId().equals(tenantId)) {
            throw new BusinessException("Assigned user must belong to the same tenant");
        }
        this.assignedTo = assignedTo;
        this.status = TaskStatus.TODO;
    }

    public static Task create(UUID tenantId, String title, String description, TaskPriority priority, OffsetDateTime dueDate, User assignedTo) {
        return new Task(tenantId, title, description, priority, dueDate, assignedTo);
    }

    /**
     * Business logic for task completion with state validation.
     */
    public void complete() {
        if (this.status == TaskStatus.DONE) {
            throw new BusinessException("Task is already completed");
        }
        if (this.status == TaskStatus.ARCHIVED) {
            throw new BusinessException("Cannot complete an archived task");
        }
        this.status = TaskStatus.DONE;
    }

    public void archive() {
        this.status = TaskStatus.ARCHIVED;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public TaskPriority getPriority() { return priority; }
    public OffsetDateTime getDueDate() { return dueDate; }
    public User getAssignedTo() { return assignedTo; }
}