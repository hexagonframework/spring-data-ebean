package org.springframework.data.ebean.sample.domain;

import org.springframework.data.ebean.domain.DomainEvent;

/**
 * @author Xuegui Yuan
 */
public class UserEmailChangedEvent extends DomainEvent {
  public UserEmailChangedEvent(Object source) {
    super(source);
  }
}
