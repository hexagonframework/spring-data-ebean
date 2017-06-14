package org.springframework.data.ebean.domain;

/**
 * DomainEvent publisher interface.
 *
 * @author Xuegui Yuan
 */
public interface DomainEventPublisher<T extends DomainEvent> {

    String identify();

    void register(Object listener);

    void publish(T event);

    void asyncPublish(T event);
}
