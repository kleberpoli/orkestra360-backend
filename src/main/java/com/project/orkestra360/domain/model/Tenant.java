package com.project.orkestra360.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import com.project.orkestra360.exception.BusinessException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents a tenant in the system.
 * 
 * Tenants are the top-level organizational units that can have multiple users
 * and tasks associated with them.
 */
@Entity
@Table(name = "tenants")
@SQLDelete(sql = "UPDATE tenants SET active = false WHERE id = ?")
@SQLRestriction("active = true")
public class Tenant extends AbstractAggregateRoot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    private String name;

    @NotBlank
    @Size(max = 255)
    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Default constructor for JPA.
     */
    protected Tenant() {
    }

    /**
     * Private constructor for creating a new tenant.
     */
    private Tenant(String name, String slug) {
        this.name = validateName(name);
        this.slug = normalizeSlug(slug);
        this.active = true;
    }

    /**
     * Factory method for creating a new tenant with validation.
     */
    public static Tenant create(String name, String slug) {
        if (name == null || slug == null) {
            throw new BusinessException("Tenant data cannot be null");
        }
        return new Tenant(name.trim(), slug.trim());
    }

    /**
     * Renames the tenant with validation.
     */
    public void rename(String name, String slug) {
        if (!this.active) {
            throw new BusinessException("Cannot rename an inactive tenant");
        }
        this.name = validateName(name);
        this.slug = normalizeSlug(slug);
    }

    /**
     * Activates the tenant. Once a tenant is deactivated, it cannot be reactivated
     * to prevent data integrity issues.
     */
    public void activate() {
        if (this.active) {
            throw new BusinessException("Tenant is already active");
        }
        this.active = true;
    }

    /**
     * Deactivates the tenant. Once a tenant is deactivated, it cannot be
     * reactivated to prevent data integrity issues. Deactivation is a soft delete
     * that marks the tenant as inactive without removing it from the database,
     * allowing for historical data retention and auditability.
     */
    public void deactivate() {
        if (!this.active) {
            throw new BusinessException("Tenant is already inactive");
        }
        this.active = false;
    }

    /**
     * Validates the tenant name according to business rules.
     */
    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Tenant name is required");
        }
        String trimmed = name.trim();
        if (trimmed.length() < 3 || trimmed.length() > 50) {
            throw new BusinessException("Tenant name must be between 3 and 50 characters");
        }
        return trimmed;
    }

    /**
     * Normalizes the tenant slug according to business rules.
     */
    private String normalizeSlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new BusinessException("Tenant slug is required");
        }
        String normalized = slug.trim().toLowerCase();
        if (normalized.length() > 255) {
            throw new BusinessException("Tenant slug must be 255 characters or less");
        }
        return normalized;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return active;
    }
}