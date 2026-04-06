package com.project.orkestra360.domain.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Class representing a composite key for the User entity, consisting of the
 * user ID and tenant ID.
 * 
 * This is necessary to ensure that the combination of user ID and tenant ID is
 * unique across the system, especially in a multi-tenant architecture where the
 * same user ID might exist in different tenants.
 * 
 * The UserId class implements Serializable to allow it to be used as a
 * composite key in JPA entities. It should also implement equals() and
 * hashCode() methods to ensure proper behavior when used in collections or as
 * keys in maps.
 */
public class UserId implements Serializable {

    private UUID id;
    private UUID tenantId;

    public UserId() {
    }

    public UserId(UUID id, UUID tenantId) {
        this.id = id;
        this.tenantId = tenantId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserId userId = (UserId) o;
        if (!id.equals(userId.id))
            return false;
        return tenantId.equals(userId.tenantId);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + tenantId.hashCode();
        return result;
    }
}