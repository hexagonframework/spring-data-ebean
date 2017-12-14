package org.springframework.data.ebean.sample.domain;

import org.springframework.data.ebean.domain.DomainService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author Xuegui Yuan
 */
@DomainService
public class UserDomainService {

  @Async
  @TransactionalEventListener
  public UserEmailChangedEvent handleUserEmailChanged(UserEmailChangedEvent emailChangedEvent) {
    System.out.println("UserEmailChangedEvent:" + emailChangedEvent.getSource());
    return null;
  }
}
