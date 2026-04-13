package com.project.orkestra360.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.project.orkestra360.dto.request.AssignTaskRequest;
import com.project.orkestra360.dto.request.TaskRequest;
import com.project.orkestra360.dto.request.UpdateTaskRequest;
import com.project.orkestra360.dto.request.UpdateTaskStatusRequest;
import com.project.orkestra360.dto.response.TaskResponse;
import com.project.orkestra360.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller for task lifecycle management within a tenant context.
 *
 * <p>All endpoints follow tenant-first URL semantics:
 * {@code /api/v1/tenants/{tenantId}/tasks}. This explicit scoping guarantees
 * that data boundaries are visible at the URL level and prevents accidental
 * cross-tenant access.</p>
 *
 * <p>State transitions (status changes, archiving) are modelled as separate
 * sub-resource actions ({@code PATCH .../status}, {@code DELETE}) rather than
 * being folded into the generic update endpoint. This aligns with the Rich
 * Domain Model: the domain enforces what transitions are valid, and the API
 * surface reflects that intent clearly.</p>
 *
 * <p>The controller is intentionally thin — it delegates all business rules to
 * {@link TaskService} and relies on
 * {@link com.project.orkestra360.exception.GlobalExceptionHandler} for uniform
 * error formatting.</p>
 *
 * <p>Base path: {@code /api/v1/tenants/{tenantId}/tasks}</p>
 */
@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/tasks")
@Tag(name = "Tasks", description = "Endpoints for task lifecycle management within a tenant")
public class TaskController {

    private final TaskService taskService;

    /**
     * Constructor-based injection preferred over {@code @Autowired} / Lombok in
     * controllers to ensure accurate JaCoCo code-coverage instrumentation.
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // ─── Read Operations ──────────────────────────────────────────────────────

    /**
     * Lists all active tasks belonging to the given tenant.
     *
     * @param tenantId the UUID of the owning tenant
     * @return a (possibly empty) list of tasks scoped to the tenant
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "List tasks by tenant",
        description = "Returns all active (non-archived) tasks that belong to the specified tenant."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Tenant not found", content = @Content)
    })
    public List<TaskResponse> getTasksByTenant(
            @Parameter(description = "UUID of the owning tenant", required = true)
            @PathVariable UUID tenantId) {
        return taskService.getTasksByTenant(tenantId)
                .stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    /**
     * Retrieves a single task by tenant and task ID.
     *
     * @param tenantId the UUID of the owning tenant
     * @param taskId   the UUID of the task to retrieve
     * @return the matching task details
     */
    @GetMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get task by ID",
        description = "Fetches a task using its UUID, enforcing tenant isolation."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Task found",
            content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "404", description = "Task not found within the tenant", content = @Content)
    })
    public TaskResponse getTaskById(
            @Parameter(description = "UUID of the owning tenant", required = true)
            @PathVariable UUID tenantId,
            @Parameter(description = "UUID of the task", required = true)
            @PathVariable UUID taskId) {
        return TaskResponse.fromEntity(taskService.getTaskById(tenantId, taskId));
    }

    // ─── Write Operations ─────────────────────────────────────────────────────

    /**
     * Creates a new task within the specified tenant.
     *
     * <p>The task starts in {@code TODO} status. Priority defaults to
     * {@code MEDIUM} if not provided. Assignment and due date are optional.</p>
     *
     * @param tenantId the UUID of the owning tenant
     * @param request  the validated creation payload
     * @return the created task with its generated UUID, timestamps, and default status
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new task",
        description = "Creates a task within the specified tenant. Status defaults to TODO; priority defaults to MEDIUM."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Task created successfully",
            content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
        @ApiResponse(responseCode = "404", description = "Tenant or assigned user not found", content = @Content),
        @ApiResponse(responseCode = "422", description = "Business rule violation (e.g. assigned user belongs to a different tenant)", content = @Content)
    })
    public TaskResponse createTask(
            @Parameter(description = "UUID of the owning tenant", required = true)
            @PathVariable UUID tenantId,
            @Valid @RequestBody TaskRequest request) {
        return TaskResponse.fromEntity(
                taskService.createTask(
                        tenantId,
                        request.title(),
                        request.description(),
                        request.priority(),
                        request.dueDate(),
                        request.assignedUserId()));
    }

    /**
     * Updates a task's mutable details (title, description, priority, due date).
     *
     * <p>Assignment changes must go through the dedicated
     * {@code PATCH .../assign} endpoint. Status changes must go through
     * {@code PATCH .../status}. Archived tasks cannot be updated.</p>
     *
     * @param tenantId the UUID of the owning tenant
     * @param taskId   the UUID of the task to update
     * @param request  the validated update payload
     * @return the updated task details
     */
    @PutMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Update task details",
        description = "Updates the title, description, priority, and due date of a task. Archived tasks cannot be modified."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Task updated successfully",
            content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task not found within the tenant", content = @Content),
        @ApiResponse(responseCode = "422", description = "Task is archived and cannot be modified", content = @Content)
    })
    public TaskResponse updateTask(
            @Parameter(description = "UUID of the owning tenant", required = true)
            @PathVariable UUID tenantId,
            @Parameter(description = "UUID of the task to update", required = true)
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        return TaskResponse.fromEntity(
                taskService.updateTaskDetails(
                        tenantId,
                        taskId,
                        request.title(),
                        request.description(),
                        request.priority(),
                        request.dueDate()));
    }

    /**
     * Transitions a task to a new status.
     *
     * <p>Valid transitions are enforced by the domain state machine defined in
     * {@link com.project.orkestra360.domain.enums.TaskStatus#canTransitionTo}.
     * For example, {@code DONE → TODO} is not allowed.</p>
     *
     * @param tenantId the UUID of the owning tenant
     * @param taskId   the UUID of the task to update
     * @param request  the validated status transition payload
     * @return the updated task details reflecting the new status
     */
    @PatchMapping("/{taskId}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Update task status",
        description = "Transitions a task to a new status. Invalid transitions (e.g. DONE → TODO) are rejected with 422."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Task status updated successfully",
            content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task not found within the tenant", content = @Content),
        @ApiResponse(responseCode = "422", description = "Invalid status transition", content = @Content)
    })
    public TaskResponse updateTaskStatus(
            @Parameter(description = "UUID of the owning tenant", required = true)
            @PathVariable UUID tenantId,
            @Parameter(description = "UUID of the task", required = true)
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request) {
        return TaskResponse.fromEntity(taskService.updateTaskStatus(tenantId, taskId, request.status()));
    }

    /**
     * Assigns a task to a user within the same tenant.
     *
     * <p>The assignee must be an active user belonging to the same tenant as
     * the task. Archived tasks cannot be re-assigned.</p>
     *
     * @param tenantId the UUID of the owning tenant
     * @param taskId   the UUID of the task to assign
     * @param request  the validated assignment payload containing the target user ID
     * @return the updated task details reflecting the new assignee
     */
    @PatchMapping("/{taskId}/assign")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Assign task to a user",
        description = "Assigns the task to an active user within the same tenant. Archived tasks cannot be assigned."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Task assigned successfully",
            content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task or user not found within the tenant", content = @Content),
        @ApiResponse(responseCode = "422", description = "Business rule violation (e.g. user belongs to a different tenant, task is archived)", content = @Content)
    })
    public TaskResponse assignTask(
            @Parameter(description = "UUID of the owning tenant", required = true)
            @PathVariable UUID tenantId,
            @Parameter(description = "UUID of the task to assign", required = true)
            @PathVariable UUID taskId,
            @Valid @RequestBody AssignTaskRequest request) {
        return TaskResponse.fromEntity(taskService.assignTask(tenantId, taskId, request.userId()));
    }

    /**
     * Archives (soft-deletes) a task, transitioning it to a terminal state.
     *
     * <p>Archiving is irreversible via the API. The task record is retained for
     * audit and history, but no further modifications are allowed once archived.</p>
     *
     * @param tenantId the UUID of the owning tenant
     * @param taskId   the UUID of the task to archive
     */
    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Archive a task",
        description = "Permanently archives the task. Archived tasks are read-only and cannot be modified or re-activated."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Task archived successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task not found within the tenant", content = @Content),
        @ApiResponse(responseCode = "422", description = "Task is already archived", content = @Content)
    })
    public void archiveTask(
            @Parameter(description = "UUID of the owning tenant", required = true)
            @PathVariable UUID tenantId,
            @Parameter(description = "UUID of the task to archive", required = true)
            @PathVariable UUID taskId) {
        taskService.archiveTask(tenantId, taskId);
    }
}
