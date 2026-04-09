package com.project.orkestra360.domain.model;

import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.project.orkestra360.domain.value.EmailAddress;
import com.project.orkestra360.domain.value.PhoneNumber;
import com.project.orkestra360.exception.BusinessException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents a user in the system.
 * 
 * Users are individuals who can interact with the application and perform
 * actions within their tenant boundaries.
 */
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = { "tenant_id", "email" }))
@SQLDelete(sql = "UPDATE users SET active = false WHERE id = ?")
@SQLRestriction("active = true")
public class User extends BaseEntity {

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String email;

    @Size(max = 20)
    private String phone;

    /**
     * Default constructor for JPA.
     */
    protected User() {
        super();
    }

    /**
     * Factory method for creating a new user with validation.
     */
    private User(UUID tenantId, String name, String email, String phone) {
        super(tenantId); // Enforce tenant scoping at the entity level
        this.name = validateName(name);
        this.email = EmailAddress.of(email).value();
        this.phone = normalizePhone(phone);
    }

    /**
     * Factory method for creating a new user with validation and tenant scoping.
     */
    public static User create(UUID tenantId, String name, String email, String phone) {
        return new User(tenantId, name, email, phone);
    }

    /**
     * Updates the user's contact information with validation.
     * 
     * This method should be used in service layer methods that allow users to
     * update their profile information, ensuring that all business rules are
     * consistently applied and that tenant isolation is maintained.
     */
    public void updateContactInformation(String name, String email, String phone) {
        this.name = validateName(name);
        this.email = EmailAddress.of(email).value();
        this.phone = normalizePhone(phone);
    }

    /**
     * Checks if the user belongs to the specified tenant.
     * 
     * This is a critical method for enforcing tenant isolation and should be used
     * in all service methods that operate on users to ensure data integrity and
     * security.
     */
    public boolean belongsTo(UUID tenantId) {
        return getTenantId() != null && getTenantId().equals(tenantId);
    }

    /**
     * Validates the user name according to business rules.
     */
    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("User name is required");
        }
        if (name.length() > 255) {
            throw new BusinessException("User name must be 255 characters or less");
        }
        return name.trim();
    }

    /**
     * Normalizes the phone number according to business rules.
     */
    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        return PhoneNumber.of(phone).value();
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}