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

import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.UpdatedTimestamp;
import io.ebean.annotation.WhoCreated;
import io.ebean.annotation.WhoModified;
import org.springframework.data.domain.Auditable;
import org.springframework.data.domain.Persistable;
import org.springframework.util.ClassUtils;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.Optional;


/**
 * Abstract base class for entities.
 * {@link #equals(Object)} and {@link #hashCode()} based on that id.
 *
 * @author Xuegui Yuan
 */
@MappedSuperclass
public abstract class AbstractEntity implements Persistable<Long>, Auditable<String, Long, LocalDateTime> {

    private static final long serialVersionUID = -5554308939380869754L;

    @Id
    protected Long id;

    @WhoCreated
    String createdBy;

    @CreatedTimestamp
    LocalDateTime createdDate;

    @WhoModified
    String lastModifiedBy;

    @UpdatedTimestamp
    LocalDateTime lastModifiedDate;

    @Override
    public Optional<String> getCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Optional<LocalDateTime> getCreatedDate() {
        return Optional.ofNullable(createdDate);
    }

    @Override
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public Optional<String> getLastModifiedBy() {
        return Optional.ofNullable(lastModifiedBy);
    }

    @Override
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public Optional<LocalDateTime> getLastModifiedDate() {
        return Optional.ofNullable(lastModifiedDate);
    }

    @Override
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public int hashCode() {
        int hashCode = 17;

        hashCode += null == getId() ? 0 : getId().hashCode() * 31;

        return hashCode;
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * Sets the id of the entity.
     *
     * @param id the id to set
     */
    public void setId(final Long id) {
        this.id = id;
    }

    @Transient
    @Override
    public boolean isNew() {
        return null == getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(ClassUtils.getUserClass(obj))) {
            return false;
        }

        AbstractEntity that = (AbstractEntity) obj;

        return null != this.getId() && this.getId().equals(that.getId());
    }

    @Override
    public String toString() {
        return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
    }

}
