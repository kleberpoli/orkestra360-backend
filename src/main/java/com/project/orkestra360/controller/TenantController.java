package com.project.orkestra360.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.project.orkestra360.dto.response.TenantResponse;
import com.project.orkestra360.service.TenantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for managing tenants in the Orkestra360 application. This
 * controller provides endpoints for tenant lifecycle management, including
 * creation, retrieval, updating, and deletion of tenant information.
 * 
 * Technical Note: The controller is kept thin by delegating error handling to a
 * Global Exception Handler.
 */
@RestController
@RequestMapping("/tenants")
@Tag(name = "Tenants", description = "Endpoints for tenant lifecycle management")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    /**
     * Retrieves a tenant by its internal ID.
     * 
     * @param id The unique internal identifier of the tenant.
     * @return A TenantResponse DTO containing the tenant details.
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get tenant by ID", description = "Fetches a tenant using its ID.")
    public TenantResponse getTenantById(@PathVariable String id) {
        return TenantResponse.fromEntity(tenantService.getTenantById(UUID.fromString(id)));
    }
}