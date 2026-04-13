package com.project.orkestra360.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.orkestra360.domain.events.DomainEventPublisher;
import com.project.orkestra360.domain.events.TaskCreatedEvent;
import com.project.orkestra360.domain.model.Task;
import com.project.orkestra360.domain.model.User;
import com.project.orkestra360.domain.enums.TaskPriority;
import com.project.orkestra360.domain.enums.TaskStatus;
import com.project.orkestra360.exception.ResourceNotFoundException;
import com.project.orkestra360.repository.TaskRepository;
import com.project.orkestra360.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Application service for task lifecycle orchestration.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;

    /**
     * Retrieves a task by tenant ID and task ID, ensuring tenant isolation.
     * 
     * @param tenantId the ID of the tenant to which the task belongs
     * @param taskId   the ID of the task to find
     * @return the found task, or throws ResourceNotFoundException if not found
     */
    @Transactional(readOnly = true)
    public Task getTaskById(UUID tenantId, UUID taskId) {
        return taskRepository.findByTenantIdAndId(tenantId, taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found for tenant %s and id %s".formatted(tenantId, taskId)));
    }

    /**
     * Retrieves all tasks belonging to a specific tenant, ensuring tenant
     * isolation.
     * 
     * @param tenantId the ID of the tenant for which to find tasks
     * @return a list of tasks belonging to the specified tenant
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByTenant(UUID tenantId) {
        return taskRepository.findAllByTenantId(tenantId);
    }

    /**
     * Creates a new task within a tenant, optionally assigning it to a user.
     * 
     * @param tenantId       the ID of the tenant to which the task will belong
     * @param title          the title of the task
     * @param description    the description of the task
     * @param priority       the priority level of the task
     * @param dueDate        the due date of the task
     * @param assignedUserId the ID of the user to assign the task to (optional)
     * @return the created task
     */
    @Transactional
    public Task createTask(UUID tenantId, String title, String description, TaskPriority priority,
            OffsetDateTime dueDate, UUID assignedUserId) {

        User assignee = null;

        // If an assigned user ID is provided, validate that the user exists within the
        // tenant
        if (assignedUserId != null) {
            assignee = userRepository.findByTenantIdAndId(tenantId, assignedUserId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Assigned user not found in tenant: %s".formatted(assignedUserId)));
        }

        // Create the task using the factory method, which includes validation and
        // default value setting
        Task task = Task.create(tenantId, title, description, priority, dueDate, assignee);

        // Save the task to the repository, which will generate an ID and persist it
        Task saved = taskRepository.save(task);

        // Publish a TaskCreatedEvent with relevant details for downstream processing
        eventPublisher.publish(
                new TaskCreatedEvent(saved.getTenantId(), saved.getId(), saved.getTitle(), saved.getDueDate()));
        eventPublisher.publish(saved.pullDomainEvents());
        return saved;
    }

    /**
     * Updates the status of a task, enforcing valid state transitions and tenant
     * isolation.
     * 
     * @param tenantId     the ID of the tenant to which the task belongs
     * @param taskId       the ID of the task to update
     * @param targetStatus the desired status for the task
     * @return the updated task
     */
    @Transactional
    public Task updateTaskStatus(UUID tenantId, UUID taskId, TaskStatus targetStatus) {

        // Retrieve the task, ensuring it belongs to the tenant and exists
        Task task = getTaskById(tenantId, taskId);

        // Update the task status, which includes validation for valid state transitions
        task.updateStatus(targetStatus);

        // Save the updated task and publish any domain events that were registered
        // during the status update
        Task saved = taskRepository.save(task);
        eventPublisher.publish(saved.pullDomainEvents());
        return saved;
    }

    /**
     * Assigns a task to a user, ensuring that the user belongs to the same tenant
     * and that the task is not archived.
     * 
     * @param tenantId the ID of the tenant to which the task belongs
     * @param taskId   the ID of the task to assign
     * @param userId   the ID of the user to whom the task should be assigned
     * @return the updated task
     */
    @Transactional
    public Task assignTask(UUID tenantId, UUID taskId, UUID userId) {

        // Retrieve the task, ensuring it belongs to the tenant and exists
        Task task = getTaskById(tenantId, taskId);

        // Validate that the user exists within the tenant before assigning
        User user = userRepository.findByTenantIdAndId(tenantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found for tenant %s and id %s".formatted(tenantId, userId)));

        // Assign the task to the user, which includes validation for task state and
        // tenant isolation
        task.assignTo(user);

        // Save the updated task and publish any domain events that were registered
        // during the assignment
        Task saved = taskRepository.save(task);
        eventPublisher.publish(saved.pullDomainEvents());
        return saved;
    }

    /**
     * Updates the details of a task, enforcing tenant isolation and validating
     * input.
     *
     * @param tenantId    the ID of the tenant to which the task belongs
     * @param taskId      the ID of the task to update
     * @param title       the new title of the task
     * @param description the new description of the task
     * @param priority    the new priority level of the task
     * @param dueDate     the new due date of the task
     * @return the updated task
     */
    @Transactional
    public Task updateTaskDetails(UUID tenantId, UUID taskId, String title, String description, TaskPriority priority,
            OffsetDateTime dueDate) {

        // Retrieve the task, ensuring it belongs to the tenant and exists
        Task task = getTaskById(tenantId, taskId);

        // Update the task details, which includes validation for input and task state
        task.updateDetails(title, description, priority, dueDate);

        // Save the updated task and publish any domain events that were registered
        // during the update
        Task saved = taskRepository.save(task);
        eventPublisher.publish(saved.pullDomainEvents());
        return saved;
    }

    /**
     * Archives a task, transitioning it to a terminal, immutable state.
     *
     * <p>Archiving is the soft-delete equivalent for tasks: the record is
     * retained for audit and history, but no further modifications are allowed.</p>
     *
     * @param tenantId the ID of the tenant to which the task belongs
     * @param taskId   the ID of the task to archive
     * @throws ResourceNotFoundException if no task is found for the given tenant and ID
     * @throws BusinessException         if the task is already archived
     */
    @Transactional
    public void archiveTask(UUID tenantId, UUID taskId) {
        Task task = getTaskById(tenantId, taskId);
        task.archive();
        Task saved = taskRepository.save(task);
        eventPublisher.publish(saved.pullDomainEvents());
    }
}