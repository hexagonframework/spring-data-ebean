package org.springframework.data.ebean.domain;

import java.util.Date;

/**
 * DomainEvent base class.
 *
 * @author Xuegui Yuan
 */
public abstract class DomainEvent {
    private Date occurredTime;

    public DomainEvent() {
        occurredTime = new Date();
    }

    protected abstract String identify();

    public Date getOccurredTime() {
        return occurredTime;
    }
}