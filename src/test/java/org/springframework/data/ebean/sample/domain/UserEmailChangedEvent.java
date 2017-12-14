package org.springframework.data.ebean.sample.domain;

import lombok.Data;
import org.springframework.data.ebean.domain.DomainEvent;

/**
 * @author Xuegui Yuan
 */
@Data
public class UserEmailChangedEvent extends DomainEvent {
    public UserEmailChangedEvent(Object source) {
        super(source);
    }
}
