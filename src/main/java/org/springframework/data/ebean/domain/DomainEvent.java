package org.springframework.data.ebean.domain;

import java.util.Date;

/**
 * DomainEvent interface.
 *
 * @author Xuegui Yuan
 */
public interface DomainEvent {
  /**
   * Occurred date.
   *
   * @return Occurred date
   */
  Date occurredOn();
}