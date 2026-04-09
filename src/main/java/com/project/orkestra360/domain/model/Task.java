package com.project.orkestra360.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.project.orkestra360.domain.enums.TaskPriority;
import com.project.orkestra360.domain.enums.TaskStatus;
import com.project.orkestra360.domain.events.TaskAssignmentChangedEvent;
import com.project.orkestra360.domain.events.TaskStatusChangedEvent;
import com.project.orkestra360.exception.BusinessException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents a task in the system.
 * 
 * Tasks are the primary unit of work and enforce workflow, assignment, and
 * tenant-scoped integrity rules.
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

    /**
     * Default constructor for JPA.
     */
    protected Task() {
        super();
    }

    /**
     * Private constructor for creating a new Task with all required fields and
     * optional details.
     */
    private Task(UUID tenantId, String title, String description, TaskPriority priority, OffsetDateTime dueDate,
            User assignedTo) {
        super(tenantId);
        this.title = validateTitle(title);
        this.description = validateDescription(description);
        this.priority = (priority != null) ? priority : TaskPriority.MEDIUM;
        this.dueDate = dueDate;
        setAssignedUser(assignedTo, tenantId);
        this.status = TaskStatus.TODO;
    }

    /**
     * Factory method for creating a new Task with validation and default values for
     * optional fields.
     */
    public static Task create(UUID tenantId, String title, String description, TaskPriority priority,
            OffsetDateTime dueDate, User assignedTo) {
        return new Task(tenantId, title, description, priority, dueDate, assignedTo);
    }

    /**
     * Updates the details of the task.
     */
    public void updateDetails(String title, String description, TaskPriority priority, OffsetDateTime dueDate) {
        ensureNotArchived();
        this.title = validateTitle(title);
        this.description = validateDescription(description);
        if (priority != null) {
            this.priority = priority;
        }
        this.dueDate = dueDate;
    }

    /**
     * Updates the status of the task, enforcing valid state transitions.
     */
    public void updateStatus(TaskStatus newStatus) {
        if (newStatus == null) {
            throw new BusinessException("Task status is required");
        }
        if (newStatus == this.status) {
            return;
        }
        if (!this.status.canTransitionTo(newStatus)) {
            throw new BusinessException(
                    "Invalid status transition from %s to %s".formatted(this.status, newStatus));
        }
        TaskStatus previous = this.status;
        this.status = newStatus;

        // Publish a domain event for the status change
        registerEvent(new TaskStatusChangedEvent(getTenantId(), getId(), previous, this.status));
    }

    /**
     * Assigns the task to a user, ensuring that the user belongs to the same tenant
     * and that the task is not archived.
     */
    public void assignTo(User assignee) {
        ensureNotArchived();
        if (assignee == null) {
            throw new BusinessException("Assigned user cannot be null");
        }
        if (!assignee.getTenantId().equals(getTenantId())) {
            throw new BusinessException("Assigned user must belong to the same tenant");
        }

        // Check if the assignment is actually changing to avoid unnecessary events
        UUID previousAssignee = assignedTo != null ? assignedTo.getId() : null;
        if (Objects.equals(previousAssignee, assignee.getId())) {
            return;
        }

        // Set the new assignee and publish a domain event for the assignment change
        setAssignedUser(assignee, getTenantId());
        registerEvent(new TaskAssignmentChangedEvent(getTenantId(), getId(), assignee.getId()));
    }

    /**
     * Unassigns the task from its current assignee.
     */
    public void unassign() {
        ensureNotArchived();
        if (this.assignedTo == null) {
            return;
        }

        // Clear the assignee and publish a domain event for the unassignment
        this.assignedTo = null;
        registerEvent(new TaskAssignmentChangedEvent(getTenantId(), getId(), null));
    }

    /**
     * Marks the task as complete.
     */
    public void complete() {
        updateStatus(TaskStatus.DONE);
    }

    /**
     * Archives the task.
     * 
     * Archiving a task is a special case that bypasses normal status transition
     * rules, but still requires that the task is not already archived.
     */
    public void archive() {
        if (this.status == TaskStatus.ARCHIVED) {
            throw new BusinessException("Task is already archived");
        }
        TaskStatus previousStatus = this.status;
        this.status = TaskStatus.ARCHIVED;
        registerEvent(new TaskStatusChangedEvent(getTenantId(), getId(), previousStatus, TaskStatus.ARCHIVED));
    }

    /**
     * Determines if the task is currently assigned to a user.
     */
    public boolean isAssigned() {
        return assignedTo != null;
    }

    /**
     * Determines if the task is overdue based on the current date and time compared
     * to the due date, and considering only tasks that are not completed or
     * archived.
     */
    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(OffsetDateTime.now()) && status != TaskStatus.DONE
                && status != TaskStatus.ARCHIVED;
    }

    /**
     * Sets the assigned user for this task, ensuring that the user belongs to the
     * same tenant.
     */
    private void setAssignedUser(User assignee, UUID tenantId) {
        if (assignee != null && !assignee.getTenantId().equals(tenantId)) {
            throw new BusinessException("Assigned user must belong to the same tenant");
        }
        this.assignedTo = assignee;
    }

    /**
     * Ensures that the task is not archived before allowing modifications.
     * 
     * Archived tasks are immutable and cannot be updated, assigned, or have their
     * status changed. This method should be called at the beginning of any
     * operation that modifies the task to enforce this business rule.
     */
    private void ensureNotArchived() {
        if (this.status == TaskStatus.ARCHIVED) {
            throw new BusinessException("Cannot modify an archived task");
        }
    }

    /**
     * Validates the task title according to business rules.
     */
    private String validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new BusinessException("Task title is required");
        }
        if (title.length() > 255) {
            throw new BusinessException("Task title must be 255 characters or less");
        }
        return title.trim();
    }

    /**
     * Validates the task description according to business rules.
     */
    private String validateDescription(String description) {
        if (description == null) {
            return null;
        }
        if (description.length() > 1000) {
            throw new BusinessException("Task description must be 1000 characters or less");
        }
        return description.trim();
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public OffsetDateTime getDueDate() {
        return dueDate;
    }

    public User getAssignedTo() {
        return assignedTo;
    }
}