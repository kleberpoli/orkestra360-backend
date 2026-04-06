-- 
-- Migration script to create the initial database schema for Orkestra360 application.
-- This script sets up the necessary tables, constraints, and indexes to support the 
-- multi-tenant architecture of the application.
-- 

-- Enable UUID generation extension (PostgreSQL)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

---
--- TABLE: tenants
---
CREATE TABLE tenants (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(50) NOT NULL,
    slug        VARCHAR(255) NOT NULL,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Unique constraint to ensure that each tenant has a unique slug, which is important for 
    -- URL routing and tenant identification.
    CONSTRAINT uk_tenants_slug UNIQUE (slug),

    -- Check constraint to enforce that tenant names are not empty or just whitespace, ensuring 
    -- data integrity and meaningful tenant records.
    CONSTRAINT ck_tenants_name_not_empty CHECK (length(trim(name)) > 0)
);

-- Index to optimize lookups by slug, which is commonly used for tenant 
-- identification in URLs and API calls.
CREATE INDEX idx_tenants_slug ON tenants(slug);

---
--- TABLE: users
---
CREATE TABLE users (
    id  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id   UUID NOT NULL,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    phone       VARCHAR(20),
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version     BIGINT NOT NULL DEFAULT 1,

    -- Composite unique constraint to ensure that user IDs are unique within the same tenant,
    -- allowing for the same user ID to exist across different tenants while maintaining 
    -- uniqueness within each tenant. This is crucial for multi-tenancy data integrity and isolation.
    CONSTRAINT uk_users_id_tenant UNIQUE (id, tenant_id),

    -- Foreign key constraint to ensure that each user is associated with a valid tenant, enforcing
    -- multi-tenancy data integrity. The ON DELETE RESTRICT action prevents deletion of a tenant.
    CONSTRAINT fk_users_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE RESTRICT,

    -- Unique constraint to ensure that email addresses are unique within the same tenant, allowing for 
    -- the same email to exist across different tenants while maintaining uniqueness within each tenant. 
    -- This is important for user authentication and communication purposes, ensuring that each user can 
    -- be uniquely identified by their email within their tenant.
    CONSTRAINT uk_users_email_tenant UNIQUE (tenant_id, email),

    -- Check constraint to enforce that email addresses are stored in lowercase, ensuring consistency and 
    -- simplifying case-insensitive lookups. This helps maintain data integrity and prevents issues with 
    -- duplicate emails that differ only in case, which is important for user authentication and communication.
    CONSTRAINT ck_users_email_lowercase CHECK (email = lower(email))
);

-- Index to optimize lookups by tenant_id and active status, which are 
-- common filters for user management and authentication.
CREATE INDEX idx_users_tenant_active ON users(tenant_id, active);

---
--- TABLE: tasks
---
CREATE TABLE tasks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID NOT NULL,
    assigned_user_id    UUID,
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    status              VARCHAR(30) NOT NULL DEFAULT 'TODO',
    priority            VARCHAR(30) NOT NULL DEFAULT 'MEDIUM',
    due_date            TIMESTAMPTZ,
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version             BIGINT NOT NULL DEFAULT 1,

    -- The tenant_id of the Task must match the tenant_id of the assigned User to ensure data integrity 
    -- and proper multi-tenancy isolation.
    CONSTRAINT fk_tasks_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,

    -- The assigned_user_id is nullable to allow for unassigned tasks, but if set, it must reference 
    -- a valid user in the same tenant.
    CONSTRAINT fk_tasks_assigned_user FOREIGN KEY (assigned_user_id) REFERENCES users(id) ON DELETE SET NULL,

    -- Check constraints to enforce valid status and priority values, ensuring data integrity at the database level.
    CONSTRAINT ck_tasks_status CHECK (status IN ('TODO', 'DOING', 'DONE', 'ARCHIVED')),
    CONSTRAINT ck_tasks_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),

    -- Composite foreign key to ensure that the assigned user belongs to the same tenant 
    -- as the task, enforcing multi-tenancy data integrity.
    CONSTRAINT fk_tasks_assigned_user_tenant 
        FOREIGN KEY (assigned_user_id, tenant_id) 
        REFERENCES users(id, tenant_id) 
        ON DELETE SET NULL
);

-- Essential indexes for dashboard listing and filters
CREATE INDEX idx_tasks_tenant_status ON tasks(tenant_id, status);
CREATE INDEX idx_tasks_tenant_due_date ON tasks(tenant_id, due_date) WHERE active IS TRUE;
CREATE INDEX idx_tasks_assigned_user ON tasks(assigned_user_id);