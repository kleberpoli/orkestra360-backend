package com.project.orkestra360.repository;

import com.project.orkestra360.domain.model.Tenant;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access layer for Tenant entity. Uses Spring Data JPA to provide standard
 * CRUD and custom query derivation.
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

}