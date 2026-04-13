package com.project.orkestra360.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.orkestra360.domain.events.DomainEventPublisher;
import com.project.orkestra360.domain.events.TenantCreatedEvent;
import com.project.orkestra360.domain.model.Tenant;
import com.project.orkestra360.exception.EntityAlreadyExistsException;
import com.project.orkestra360.exception.ResourceNotFoundException;
import com.project.orkestra360.repository.TenantRepository;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Application service for tenant lifecycle orchestration.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService {

	private final TenantRepository tenantRepository;
	private final DomainEventPublisher eventPublisher;

	/**
	 * Retrieves a tenant by its unique identifier.
	 * 
	 * @param id the unique identifier of the tenant to retrieve
	 * @return the found tenant, or throws ResourceNotFoundException if not found
	 * @throws ResourceNotFoundException if no tenant is found with the given ID
	 */
	@Transactional(readOnly = true)
	public Tenant getTenantById(UUID id) {
		return tenantRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No tenant found with ID: %s".formatted(id)));
	}

	/**
	 * Retrieves a tenant by its unique slug identifier.
	 * 
	 * @param slug the unique slug of the tenant to retrieve
	 * @return the found tenant, or throws ResourceNotFoundException if not found
	 * @throws ResourceNotFoundException if no tenant is found with the given slug
	 */
	@Transactional(readOnly = true)
	public Tenant getTenantBySlug(String slug) {
		return tenantRepository.findBySlug(slug)
				.orElseThrow(() -> new ResourceNotFoundException("No tenant found with slug: %s".formatted(slug)));
	}

	/**
	 * Creates a new tenant with the given name and slug, ensuring slug uniqueness.
	 * 
	 * @param name the name of the tenant to create
	 * @param slug the unique slug for the tenant to create
	 * @return the created tenant
	 * @throws EntityAlreadyExistsException if the slug is already in use by another
	 *                                      tenant
	 */
	@Transactional
	public Tenant createTenant(String name, String slug) {

		// Validate slug uniqueness before creating the tenant
		if (tenantRepository.existsBySlug(slug)) {
			throw new EntityAlreadyExistsException("Tenant with slug %s already exists".formatted(slug));
		}

		// Create and save the new tenant
		Tenant tenant = Tenant.create(name, slug);
		Tenant saved = tenantRepository.save(tenant);

		// Publish a domain event for tenant creation
		eventPublisher.publish(new TenantCreatedEvent(saved.getId(), saved.getName(), saved.getSlug()));
		return saved;
	}

	/**
	 * Updates an existing tenant's name and slug, ensuring slug uniqueness and
	 * tenant isolation.
	 *
	 * @param id   the unique identifier of the tenant to update
	 * @param name the new name for the tenant
	 * @param slug the new unique slug for the tenant
	 * @return the updated tenant
	 * @throws ResourceNotFoundException    if no tenant is found with the given ID
	 * @throws EntityAlreadyExistsException if the new slug is already in use by
	 *                                      another
	 */
	@Transactional
	public Tenant updateTenantDetails(UUID id, String name, String slug) {

		// Retrieve the tenant to update, ensuring it exists before proceeding with
		// updates
		Tenant tenant = getTenantById(id);

		// If the slug is being updated, validate its uniqueness
		if (!tenant.getSlug().equals(slug) && tenantRepository.existsBySlugAndIdNot(slug, tenant.getId())) {
			throw new EntityAlreadyExistsException("Tenant with slug %s already exists".formatted(slug));
		}

		// Update the tenant's name and slug using the domain method, which includes
		// validation
		tenant.rename(name, slug);
		return tenantRepository.save(tenant);
	}

	/**
	 * Deactivates an existing tenant, performing a soft delete that preserves
	 * historical data while preventing further access.
	 *
	 * @param id the unique identifier of the tenant to deactivate
	 * @throws ResourceNotFoundException if no tenant is found with the given ID
	 * @throws BusinessException         if the tenant is already inactive
	 */
	@Transactional
	public void deactivateTenant(UUID id) {
		Tenant tenant = getTenantById(id);
		tenant.deactivate();
		tenantRepository.save(tenant);
	}
}