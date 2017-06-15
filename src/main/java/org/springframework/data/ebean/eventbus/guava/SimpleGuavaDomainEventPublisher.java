package org.springframework.data.ebean.eventbus.guava;

/**
 * SimpleGuavaDomainEventPublisher singleton class.
 *
 * @author Xuegui Yuan
 */
public class SimpleGuavaDomainEventPublisher extends AbstractGuavaDomainEventPublisher {

    private static SimpleGuavaDomainEventPublisher instance = new SimpleGuavaDomainEventPublisher();

    public static SimpleGuavaDomainEventPublisher getInstance() {
        return instance;
    }

    @Override
    public String identify() {
        return "simple";
    }
}
