package org.springframework.data.ebean.domain.sample;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.ebean.domain.DomainEvent;

/**
 * @author Xuegui Yuan
 */
@Data
@AllArgsConstructor
public class UserEvent extends DomainEvent {
    private String email;

    @Override
    protected String identify() {
        return "user_event";
    }
}
