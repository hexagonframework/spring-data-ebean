package org.springframework.data.ebean.domain;

import java.util.Date;

/**
 * DomainEvent interface.
 *
 * @author Xuegui Yuan
 */
public interface DomainEvent {
    Date occurredOn();
}