package com.project.orkestra360.domain.events;

import java.time.OffsetDateTime;

/**
 * Marker interface for domain events emitted by aggregates. All domain events
 * must implement this interface to be published and handled by the event bus.
 */
public interface DomainEvent {

    /**
     * Returns the timestamp when the event occurred.
     */
    OffsetDateTime occurredAt();

}