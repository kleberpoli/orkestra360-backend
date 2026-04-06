package com.project.orkestra360.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.orkestra360.domain.model.Tenant;
import com.project.orkestra360.exception.ResourceNotFoundException;
import com.project.orkestra360.repository.TenantRepository;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService {

	private final TenantRepository tenantRepository;

	/**
	 * Retrieves a tenant from the database using its technical UUID.
	 *
	 * @param id The unique internal identifier of the tenant.
	 * @return The found Tenant entity.
	 * @throws ResourceNotFoundException If no tenant matches the provided ID.
	 */
	@Transactional(readOnly = true)
	public Tenant getTenantById(UUID id) {
		return tenantRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No tenant found with ID: " + id));
	}
}