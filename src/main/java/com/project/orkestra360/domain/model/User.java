package com.project.orkestra360.domain.model;

import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Represents a user in the system.
 * Users are individuals who can interact with the application and perform
 * various actions. Each user belongs to a tenant and can be assigned tasks.
 * The User entity includes basic contact information and is designed to be
 * simple and extensible for future enhancements.
 * It also implements soft deletion to allow for data recovery and auditing.
 */
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = { "tenant_id", "email" }))
@SQLDelete(sql = "UPDATE users SET active = false WHERE id = ?")
@SQLRestriction("active = true")
public class User extends BaseEntity {

    @Override
    public UUID getId() {
        // Id comes from BaseEntity
        return super.getId();
    }

    @Override
    public UUID getTenantId() {
        // TenantId comes from BaseEntity
        return super.getTenantId();
    }

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @Email
    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String email;

    @Size(max = 20)
    private String phone;

    protected User() {
    }

    private User(UUID tenantId, String name, String email, String phone) {
        super(tenantId);
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public static User create(UUID tenantId, String name, String email, String phone) {
        return new User(tenantId, name, email, phone);
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}