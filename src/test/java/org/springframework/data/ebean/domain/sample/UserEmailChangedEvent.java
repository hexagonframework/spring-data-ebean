package org.springframework.data.ebean.domain.sample;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.ebean.domain.DomainEvent;

import java.util.Date;

/**
 * @author Xuegui Yuan
 */
@Data
@AllArgsConstructor
public class UserEmailChangedEvent implements DomainEvent {
    private Long userId;
    private String email;
    private Date createdTime;

    @Override
    public Date occurredOn() {
        return createdTime;
    }
}
