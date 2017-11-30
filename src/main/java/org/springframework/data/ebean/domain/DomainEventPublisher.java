package org.springframework.data.ebean.domain;

/**
 * DomainEvent publisher interface.
 *
 * @author Xuegui Yuan
 */
public interface DomainEventPublisher<T extends DomainEvent> {

  /**
   * Register a listener.
   *
   * @param listener
   */
  void register(Object listener);

  /**
   * Publish sync event.
   *
   * @param event
   */
  void publish(T event);

  /**
   * Publish async event.
   *
   * @param event
   */
  void asyncPublish(T event);
}
