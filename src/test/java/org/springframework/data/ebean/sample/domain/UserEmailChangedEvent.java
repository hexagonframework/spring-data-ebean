package org.springframework.data.ebean.sample.domain;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.ebean.domain.DomainEvent;

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
