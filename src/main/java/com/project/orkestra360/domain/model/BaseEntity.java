package com.project.orkestra360.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.project.orkestra360.exception.BusinessException;
import jakarta.persistence.*;

/**
 * Base class for all entities in the domain model.
 * Provides auditing, soft delete support, and optimistic locking.
 */
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Version
    private Long version;

    protected BaseEntity() {}

    /**
     * @param tenantId The owner of this entity. Mandatory for SaaS isolation.
     */
    protected BaseEntity(UUID tenantId) {
        if (tenantId == null) {
            throw new BusinessException("Tenant ID is required for entity creation");
        }
        this.tenantId = tenantId;
        this.active = true;
    }

    public void delete() {
        if (!this.active) {
            throw new BusinessException("Entity is already inactive/deleted");
        }
        this.active = false;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public boolean isActive() { return active; }
    public Long getVersion() { return version; }
}