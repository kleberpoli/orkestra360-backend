package com.project.orkestra360.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.orkestra360.domain.events.DomainEventPublisher;
import com.project.orkestra360.domain.events.UserCreatedEvent;
import com.project.orkestra360.domain.model.User;
import com.project.orkestra360.domain.value.EmailAddress;
import com.project.orkestra360.domain.value.PhoneNumber;
import com.project.orkestra360.exception.BusinessException;
import com.project.orkestra360.exception.EntityAlreadyExistsException;
import com.project.orkestra360.exception.ResourceNotFoundException;
import com.project.orkestra360.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Application service for user lifecycle orchestration within a tenant context.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;

    /**
     * Retrieves a user by tenant ID and user ID, ensuring tenant isolation.
     * 
     * @param tenantId the ID of the tenant to which the user belongs
     * @param userId   the ID of the user to find
     * @return the found user, or throws ResourceNotFoundException if not found
     * @throws ResourceNotFoundException if no user is found with the given tenant
     *                                   ID and user ID
     */
    @Transactional(readOnly = true)
    public User getUserById(UUID tenantId, UUID userId) {
        return userRepository.findByTenantIdAndId(tenantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found for tenant %s and id %s".formatted(tenantId, userId)));
    }

    /**
     * Retrieves all users belonging to a specific tenant, ensuring tenant
     * isolation.
     * 
     * @param tenantId the ID of the tenant for which to find users
     * @return a list of users belonging to the specified tenant
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByTenant(UUID tenantId) {
        return userRepository.findAllByTenantId(tenantId);
    }

    /**
     * Creates a new user within a tenant, ensuring email uniqueness and validating
     * contact information.
     * 
     * @param tenantId the ID of the tenant to which the user belongs
     * @param name     the name of the user to create
     * @param email    the email address of the user to create
     * @param phone    the phone number of the user to create
     * @return the created user
     * @throws BusinessException            if the email or phone number is invalid
     * @throws EntityAlreadyExistsException if a user with the same email already
     *                                      exists in the tenant or if contact
     *                                      information is invalid
     */
    @Transactional
    public User createUser(UUID tenantId, String name, String email, String phone) {

        // Normalize and validate the email address
        EmailAddress validatedEmail = EmailAddress.of(email);

        // Normalize and validate the phone number
        PhoneNumber validatedPhone = PhoneNumber.of(phone);

        // Check if a user with the same email already exists in the tenant
        if (userRepository.existsByTenantIdAndEmail(tenantId, validatedEmail.value())) {
            throw new EntityAlreadyExistsException("A user with this email already exists in the tenant");
        }

        // Create the user using the factory method, which includes validation and
        // default value setting
        User user = User.create(tenantId, name, validatedEmail.value(), validatedPhone.value());
        User saved = userRepository.save(user);

        // Publish a domain event for user creation, which can be used for auditing,
        // notifications, or other downstream processing
        eventPublisher.publish(new UserCreatedEvent(saved.getTenantId(), saved.getId(), saved.getEmail()));
        return saved;
    }

    /**
     * Updates a user's contact information, ensuring email uniqueness and
     * validating the new contact details.
     * 
     * @param tenantId the ID of the tenant to which the user belongs
     * @param userId   the ID of the user to update
     * @param name     the updated name of the user
     * @param email    the updated email address of the user
     * @param phone    the updated phone number of the user
     * @return the updated user
     * @throws BusinessException         if the new email or phone number is invalid
     * @throws ResourceNotFoundException if no user is found with the given tenant
     *                                   ID and user ID
     */
    @Transactional
    public User updateContactInfo(UUID tenantId, UUID userId, String name, String email, String phone) {

        // Retrieve the user to update, ensuring it belongs to the tenant and exists
        // before proceeding with updates
        User user = getUserById(tenantId, userId);

        // Normalize and validate the email address
        EmailAddress validatedEmail = EmailAddress.of(email);

        // Normalize and validate the phone number
        PhoneNumber validatedPhone = PhoneNumber.of(phone);

        // If the email is being updated, check for uniqueness within the tenant
        if (!user.getEmail().equals(validatedEmail.value())
                && userRepository.existsByTenantIdAndEmailAndIdNot(tenantId, validatedEmail.value(), user.getId())) {
            throw new EntityAlreadyExistsException("A user with this email already exists in the tenant");
        }

        // Update the user's contact information using the domain method, which includes
        // validation
        user.updateContactInformation(name, validatedEmail.value(), validatedPhone.value());
        return userRepository.save(user);
    }
}