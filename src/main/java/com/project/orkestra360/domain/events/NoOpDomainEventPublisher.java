package com.project.orkestra360.domain.events;

import org.springframework.stereotype.Component;

/**
 * No-op implementation of {@link DomainEventPublisher} for baseline project
 * wiring.
 *
 * This placeholder ensures that domain services can publish events without
 * requiring messaging infrastructure at this phase.
 */
@Component
public class NoOpDomainEventPublisher implements DomainEventPublisher {

    @Override
    public void publish(DomainEvent event) {
        // Intentionally left blank. Event pipelines can be added later.
    }
}