package com.project.orkestra360.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.orkestra360.domain.model.Task;

/**
 * Repository interface for managing tasks within tenant scope.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    /**
     * Finds a task by tenant ID and task ID, ensuring tenant isolation.
     * 
     * @param tenantId the ID of the tenant to which the task belongs
     * @param id the ID of the task to find
     * @return an Optional containing the found task, or empty if not found
     */
    Optional<Task> findByTenantIdAndId(UUID tenantId, UUID id);

    /**
     * Finds all tasks belonging to a specific tenant, ensuring tenant isolation.
     * 
     * @param tenantId the ID of the tenant for which to find tasks
     * @return a list of tasks belonging to the specified tenant
     */
    List<Task> findAllByTenantId(UUID tenantId);

    /**
     * Checks if a task exists by tenant ID and task ID, enforcing tenant isolation.
     * 
     * @param tenantId the ID of the tenant to which the task belongs
     * @param id the ID of the task to check for existence
     * @return true if a task with the specified tenant ID and task ID exists, false otherwise
     */
    boolean existsByTenantIdAndId(UUID tenantId, UUID id);
}