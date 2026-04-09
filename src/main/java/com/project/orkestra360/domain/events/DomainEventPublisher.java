package com.project.orkestra360.domain.events;

/**
 * Output port for publishing domain events to infrastructure layers.
 * 
 * Implementations of this interface are responsible for dispatching events to
 * the appropriate event bus or messaging system. This allows the domain layer
 * to remain decoupled from specific event handling mechanisms, enabling
 * flexibility in how events are processed and consumed by other parts of the
 * application or external systems.
 */
public interface DomainEventPublisher {

    /**
     * Publishes a single domain event to the event bus or messaging system.
     *
     * @param event The domain event to be published
     */
    void publish(DomainEvent event);

    /**
     * Publishes multiple domain events in a batch.
     * 
     * This default implementation iterates over the provided events and publishes
     * each one individually using the single event publish method. Implementations
     * can override this method to optimize batch publishing if supported by the
     * underlying infrastructure.
     * 
     * @param events An iterable collection of domain events to be published
     */
    default void publish(Iterable<DomainEvent> events) {
        if (events == null) {
            return;
        }
        for (DomainEvent event : events) {
            publish(event);
        }
    }
}