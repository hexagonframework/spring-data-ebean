package org.springframework.data.ebean.domain;

import org.springframework.context.ApplicationEvent;

/**
 * Domain event.
 * @author Xuegui Yuan
 */
public class DomainEvent extends ApplicationEvent {

  public DomainEvent(Object source) {
    super(source);
  }

}
