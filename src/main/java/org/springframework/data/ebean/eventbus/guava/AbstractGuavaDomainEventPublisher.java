package org.springframework.data.ebean.eventbus.guava;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.springframework.data.ebean.domain.DomainEvent;
import org.springframework.data.ebean.domain.DomainEventPublisher;

import java.util.concurrent.Executors;

/**
 * Guava implementation of DomainEventPublisher.
 *
 * @author Xuegui Yuan
 */
public abstract class AbstractGuavaDomainEventPublisher implements DomainEventPublisher {
    private EventBus syncBus = new EventBus(identify());
    private EventBus asyncBus = new AsyncEventBus(identify(), Executors.newFixedThreadPool(1));

    abstract String identify();

    @Override
    public void register(Object listener) {
        syncBus.register(listener);
        asyncBus.register(listener);
    }

    @Override
    public void publish(DomainEvent event) {
        syncBus.post(event);
    }

    @Override
    public void asyncPublish(DomainEvent event) {
        asyncBus.post(event);
    }

}