package com.project.orkestra360.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.orkestra360.domain.model.User;

/**
 * Repository interface for managing users within a tenant context.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by tenant ID and user ID, ensuring tenant isolation.
     * 
     * @param tenantId the ID of the tenant to which the user belongs
     * @param id       the ID of the user to find
     * @return an Optional containing the found user, or empty if not found
     */
    Optional<User> findByTenantIdAndId(UUID tenantId, UUID id);

    /**
     * Finds a user by tenant ID and email, ensuring tenant isolation.
     * 
     * @param tenantId the ID of the tenant to which the user belongs
     * @param email    the email of the user to find
     * @return an Optional containing the found user, or empty if not found
     */
    Optional<User> findByTenantIdAndEmail(UUID tenantId, String email);

    /**
     * Finds all users belonging to a specific tenant, ensuring tenant isolation.
     * 
     * @param tenantId the ID of the tenant for which to find users
     * @return a list of users belonging to the specified tenant
     */
    List<User> findAllByTenantId(UUID tenantId);

    /**
     * Checks if a user exists by tenant ID and email, enforcing tenant isolation.
     * 
     * @param tenantId the ID of the tenant to which the user belongs
     * @param email    the email of the user to check for existence
     * @return true if a user with the specified tenant ID and email exists, false
     *         otherwise
     */
    boolean existsByTenantIdAndEmail(UUID tenantId, String email);

    /**
     * Checks if a user exists by tenant ID and email, excluding a specific user ID.
     * This is used to allow updating a user's email to the same value without
     * triggering uniqueness violations.
     * 
     * @param tenantId the ID of the tenant to which the user belongs
     * @param email    the email of the user to check for existence
     * @param id       the ID of the user to exclude from the check
     * @return true if another user with the specified tenant ID and email exists,
     *         false otherwise
     */
    boolean existsByTenantIdAndEmailAndIdNot(UUID tenantId, String email, UUID id);
}