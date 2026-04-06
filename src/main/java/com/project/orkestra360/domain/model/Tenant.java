package com.project.orkestra360.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import com.project.orkestra360.exception.BusinessException;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Represents a tenant in the system.
 * Tenants are the top-level organizational units that can have multiple users
 * and tasks associated with them.
 */
@Entity
@Table(name = "tenants")
@SQLDelete(sql = "UPDATE tenants SET active = false WHERE id = ?")
@SQLRestriction("active = true")
public class Tenant {

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

    protected Tenant() {}

    private Tenant(String name, String slug) {
        this.name = name;
        this.slug = slug;
        this.active = true;
    }

    public static Tenant create(String name, String slug) {
        if (name == null || slug == null) throw new BusinessException("Tenant data cannot be null");
        return new Tenant(name.trim(), slug.toLowerCase().trim());
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public boolean isActive() { return active; }
}