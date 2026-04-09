package com.project.orkestra360.domain.model;

import java.util.ArrayList;
import java.util.List;

import com.project.orkestra360.domain.events.DomainEvent;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

/**
 * Base class for aggregates that can emit domain events.
 *
 * Entities extending this class can collect events internally and publish them
 * after successful persistence commits, which keeps domain behavior isolated
 * from infrastructure concerns.
 */
@MappedSuperclass
public abstract class AbstractAggregateRoot {

    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * Registers a domain event to be published after the aggregate is persisted.
     *
     * @param event The domain event to register
     */
    protected void registerEvent(DomainEvent event) {
        if (event != null) {
            domainEvents.add(event);
        }
    }

    /**
     * Retrieves and clears the list of domain events registered by this aggregate.
     *
     * This method is typically called by the infrastructure layer after a
     * successful transaction commit to publish all events emitted during the
     * aggregate's lifecycle.
     *
     * @return A list of domain events that were registered by this aggregate
     */
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    /**
     * Checks if there are any domain events registered by this aggregate.
     *
     * @return true if there are domain events to be published, false otherwise
     */
    public boolean hasDomainEvents() {
        return !domainEvents.isEmpty();
    }
}