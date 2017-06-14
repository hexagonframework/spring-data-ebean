package org.springframework.data.ebean.domain.guava;

/**
 * SimpleGuavaDomainEventPublisher singleton class.
 *
 * @author Xuegui Yuan
 */
public class SimpleGuavaDomainEventPublisher extends GuavaDomainEventPublisher {

    private static SimpleGuavaDomainEventPublisher instance = new SimpleGuavaDomainEventPublisher();

    public static SimpleGuavaDomainEventPublisher getInstance() {
        return instance;
    }

    @Override
    public String identify() {
        return "default";
    }
}
