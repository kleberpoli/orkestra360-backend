package com.project.orkestra360.controller;

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

import com.project.orkestra360.dto.request.TenantRequest;
import com.project.orkestra360.dto.response.TenantResponse;
import com.project.orkestra360.service.TenantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller for tenant lifecycle management.
 *
 * <p>All mutating operations are intentionally kept thin: input validation is
 * handled by Bean Validation ({@code @Valid}), business rules by the domain
 * layer, and error formatting by {@link com.project.orkestra360.exception.GlobalExceptionHandler}.</p>
 *
 * <p>Base path: {@code /api/v1/tenants}</p>
 */
@RestController
@RequestMapping("/api/v1/tenants")
@Tag(name = "Tenants", description = "Endpoints for tenant lifecycle management")
public class TenantController {

    private final TenantService tenantService;

    /**
     * Constructor-based injection preferred over {@code @Autowired} / Lombok in
     * controllers to ensure accurate JaCoCo code-coverage instrumentation.
     */
    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    // ─── Read Operations ──────────────────────────────────────────────────────

    /**
     * Retrieves a tenant by its internal UUID.
     *
     * @param id the tenant's unique identifier
     * @return the matching tenant details
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get tenant by ID",
        description = "Fetches an active tenant using its UUID. Returns 404 if the tenant does not exist."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tenant found",
            content = @Content(schema = @Schema(implementation = TenantResponse.class))),
        @ApiResponse(responseCode = "404", description = "Tenant not found", content = @Content)
    })
    public TenantResponse getTenantById(
            @Parameter(description = "UUID of the tenant", required = true)
            @PathVariable UUID id) {
        return TenantResponse.fromEntity(tenantService.getTenantById(id));
    }

    /**
     * Retrieves a tenant by its human-readable slug.
     *
     * @param slug the tenant's unique slug
     * @return the matching tenant details
     */
    @GetMapping("/slug/{slug}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get tenant by slug",
        description = "Fetches an active tenant using its slug identifier."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tenant found",
            content = @Content(schema = @Schema(implementation = TenantResponse.class))),
        @ApiResponse(responseCode = "404", description = "Tenant not found", content = @Content)
    })
    public TenantResponse getTenantBySlug(
            @Parameter(description = "Unique slug of the tenant", required = true)
            @PathVariable String slug) {
        return TenantResponse.fromEntity(tenantService.getTenantBySlug(slug));
    }

    // ─── Write Operations ─────────────────────────────────────────────────────

    /**
     * Creates a new tenant.
     *
     * @param request the validated creation payload
     * @return the created tenant with its generated UUID and timestamps
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new tenant",
        description = "Provisions a new tenant. The slug must be globally unique across all tenants."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tenant created successfully",
            content = @Content(schema = @Schema(implementation = TenantResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
        @ApiResponse(responseCode = "409", description = "Tenant with the given slug already exists", content = @Content)
    })
    public TenantResponse createTenant(@Valid @RequestBody TenantRequest request) {
        return TenantResponse.fromEntity(tenantService.createTenant(request.name(), request.slug()));
    }

    /**
     * Updates a tenant's name and slug.
     *
     * @param id      the UUID of the tenant to update
     * @param request the validated update payload
     * @return the updated tenant details
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Update tenant details",
        description = "Updates the name and slug of an existing tenant. The new slug must remain globally unique."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tenant updated successfully",
            content = @Content(schema = @Schema(implementation = TenantResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
        @ApiResponse(responseCode = "404", description = "Tenant not found", content = @Content),
        @ApiResponse(responseCode = "409", description = "Slug already in use by another tenant", content = @Content)
    })
    public TenantResponse updateTenant(
            @Parameter(description = "UUID of the tenant to update", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody TenantRequest request) {
        return TenantResponse.fromEntity(tenantService.updateTenantDetails(id, request.name(), request.slug()));
    }

    /**
     * Deactivates (soft-deletes) a tenant.
     *
     * <p>This operation is irreversible via the API. Historical data is preserved
     * in the database for compliance and auditing purposes.</p>
     *
     * @param id the UUID of the tenant to deactivate
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Deactivate a tenant",
        description = "Performs a soft-delete on the tenant. Data is retained for audit purposes but the tenant becomes inaccessible."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Tenant deactivated successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Tenant not found", content = @Content),
        @ApiResponse(responseCode = "422", description = "Tenant is already inactive", content = @Content)
    })
    public void deactivateTenant(
            @Parameter(description = "UUID of the tenant to deactivate", required = true)
            @PathVariable UUID id) {
        tenantService.deactivateTenant(id);
    }
}
