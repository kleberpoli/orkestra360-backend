package com.project.orkestra360.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.project.orkestra360.domain.model.Tenant;

/**
 * DTO representing a response payload for tenant-related API endpoints.
 * 
 * This record encapsulates the tenant's essential information, including its
 * unique identifier, name, slug, active status, and timestamps for creation and
 * last update.
 */
public record TenantResponse(
		UUID id,
		String name,
		String slug,
		boolean active,
		OffsetDateTime createdAt,
		OffsetDateTime updatedAt
	) {
	    /**
	     * Mapping Constructor (Static Factory Method).
	     */
	    public static TenantResponse fromEntity(Tenant tenant) {
	        return new TenantResponse(
	            tenant.getId(),
	            tenant.getName(),
	            tenant.getSlug(),
	            tenant.isActive(),
	            tenant.getCreatedAt(),
	            tenant.getUpdatedAt()
	        );
	    }
	}