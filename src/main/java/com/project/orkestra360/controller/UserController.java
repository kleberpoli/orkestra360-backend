package com.project.orkestra360.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.project.orkestra360.dto.request.UserRequest;
import com.project.orkestra360.dto.response.UserResponse;
import com.project.orkestra360.service.UserService;

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
 * REST controller for user lifecycle management within a tenant context.
 *
 * <p>All endpoints follow tenant-first URL semantics: {@code /tenants/{tenantId}/users}.
 * This explicit scoping ensures that data boundaries are visible at the URL level and
 * prevents accidental cross-tenant access.</p>
 *
 * <p>The controller is intentionally thin — it delegates all business rules to
 * {@link UserService} and relies on
 * {@link com.project.orkestra360.exception.GlobalExceptionHandler} for uniform error
 * formatting.</p>
 *
 * <p>Base path: {@code /api/v1/tenants/{tenantId}/users}</p>
 */
@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/users")
@Tag(name = "Users", description = "Endpoints for user lifecycle management within a tenant")
public class UserController {

    private final UserService userService;

    /**
     * Constructor-based injection preferred over {@code @Autowired} / Lombok in
     * controllers to ensure accurate JaCoCo code-coverage instrumentation.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ─── Read Operations ──────────────────────────────────────────────────────

    /**
     * Lists all active users belonging to the given tenant.
     *
     * @param tenantId the UUID of the owning tenant
     * @return a (possibly empty) list of users scoped to the tenant
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "List users by tenant",
        description = "Returns all active users that belong to the specified tenant."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Tenant not found", content = @Content)
    })
    public List<UserResponse> getUsersByTenant(
            @Parameter(description = "UUID of the owning tenant", required = true)
            @PathVariable UUID tenantId) {
        return userService.getUsersByTenant(tenantId)
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    /**
     * Retrieves a single user by tenant and user ID.
     *
     * @param tenantId the UUID of the owning tenant
     * @param userId   the UUID of the user to retrieve
     * @return the matching user details
     */
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get user by ID",
        description = "Fetches a user using its UUID, enforcing tenant isolation."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found within the tenant", content = @Content)
    })
    public UserResponse getUserById(
            @Parameter(description = "UUID of the owning tenant", required = true)
            @PathVariable UUID tenantId,
            @Parameter(description = "UUID of the user", required = true)
            @PathVariable UUID userId) {
        return UserResponse.fromEntity(userService.getUserById(tenantId, userId));
    }

    // ─── Write Operations ─────────────────────────────────────────────────────

    /**
     * Creates a new user within the specified tenant.
     *
     * <p>The email address must be unique within the tenant's scope.</p>
     *
     * @param tenantId the UUID of the owning tenant
     * @param request  the validated creation payload
     * @return the created user with its generated UUID and timestamps
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new user",
        description = "Provisions a new user within the specified tenant. Email must be unique per tenant."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
        @ApiResponse(responseCode = "404", description = "Tenant not found", content = @Content),
        @ApiResponse(responseCode = "409", description = "A user with this email already exists in the tenant", content = @Content)
    })
    public UserResponse createUser(
            @Parameter(description = "UUID of the owning tenant", required = true)
            @PathVariable UUID tenantId,
            @Valid @RequestBody UserRequest request) {
        return UserResponse.fromEntity(
                userService.createUser(tenantId, request.name(), request.email(), request.phone()));
    }

    /**
     * Updates a user's contact information.
     *
     * <p>If the email is being changed, uniqueness within the tenant is re-validated.</p>
     *
     * @param tenantId the UUID of the owning tenant
     * @param userId   the UUID of the user to update
     * @param request  the validated update payload
     * @return the updated user details
     */
    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Update user contact information",
        description = "Updates name, email, and phone for an existing user. The new email must remain unique within the tenant."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User updated successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found within the tenant", content = @Content),
        @ApiResponse(responseCode = "409", description = "A user with this email already exists in the tenant", content = @Content)
    })
    public UserResponse updateUser(
            @Parameter(description = "UUID of the owning tenant", required = true)
            @PathVariable UUID tenantId,
            @Parameter(description = "UUID of the user to update", required = true)
            @PathVariable UUID userId,
            @Valid @RequestBody UserRequest request) {
        return UserResponse.fromEntity(
                userService.updateContactInfo(tenantId, userId, request.name(), request.email(), request.phone()));
    }

    /**
     * Soft-deletes a user within the specified tenant.
     *
     * <p>The user record is marked as inactive and excluded from future queries,
     * but data is retained for compliance and audit purposes.</p>
     *
     * @param tenantId the UUID of the owning tenant
     * @param userId   the UUID of the user to delete
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete a user",
        description = "Performs a soft-delete on the user. Data is retained for audit purposes but the user becomes inaccessible."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found within the tenant", content = @Content)
    })
    public void deleteUser(
            @Parameter(description = "UUID of the owning tenant", required = true)
            @PathVariable UUID tenantId,
            @Parameter(description = "UUID of the user to delete", required = true)
            @PathVariable UUID userId) {
        userService.deleteUser(tenantId, userId);
    }
}