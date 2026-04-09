package com.project.orkestra360.repository;

import com.project.orkestra360.domain.model.Tenant;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing tenants in the application.
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    /**
     * Finds a tenant by its unique slug identifier.
     * 
     * @param slug the unique slug of the tenant to find
     * @return an Optional containing the found tenant, or empty if not found
     */
    Optional<Tenant> findBySlug(String slug);

    /**
     * Checks if a tenant exists by its unique slug identifier.
     * 
     * @param slug the unique slug of the tenant to check for existence
     * @return true if a tenant with the specified slug exists, false otherwise
     */
    boolean existsBySlug(String slug);

    /**
     * Checks if a tenant exists by its unique slug identifier, excluding a specific
     * tenant ID. This is used to allow updating a tenant's slug to the same value
     * without triggering uniqueness violations.
     * 
     * @param slug the unique slug of the tenant to check for existence
     * @param id   the ID of the tenant to exclude from the check
     * @return true if another tenant with the specified slug exists, false
     *         otherwise
     */
    boolean existsBySlugAndIdNot(String slug, UUID id);
}