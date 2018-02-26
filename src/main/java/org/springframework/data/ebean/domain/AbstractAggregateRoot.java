/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.ebean.domain;

import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;
import org.springframework.util.Assert;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for aggregate root, aggregate extends {@link AbstractEntity} and add
 * domain event functions.
 *
 * @author XueguiYuan
 */
@MappedSuperclass
public abstract class AbstractAggregateRoot extends AbstractEntity {
    @Transient
    private transient final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * Registers the given event object for publication on a call to a Spring Data repository's save methods.
     *
     * @param event must not be {@literal null}.
     * @return the event that has been added.
     */
    protected <T extends DomainEvent> T registerEvent(T event) {
        Assert.notNull(event, "Domain event must not be null!");

        this.domainEvents.add(event);
        return event;
    }

    /**
     * Clears all domain events currently held. Usually invoked by the infrastructure in place in Spring Data
     * repositories.
     */
    @AfterDomainEventPublication
    protected void clearDomainEvents() {
        this.domainEvents.clear();
    }

    /**
     * All domain events currently captured by the aggregate.
     */
    @DomainEvents
    protected Collection<DomainEvent> domainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

}
