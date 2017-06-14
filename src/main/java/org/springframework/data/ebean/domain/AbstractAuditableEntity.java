/*
 * Copyright 2008-2017 the original author or authors.
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
import org.joda.time.DateTime;
import org.springframework.data.domain.Auditable;

import javax.persistence.MappedSuperclass;

/**
 * Abstract base class for auditable entities. Stores the audition values in persistent fields.
 *
 * @author Xuegui Yuan
 */
@MappedSuperclass
public abstract class AbstractAuditableEntity extends AbstractEntity
        implements Auditable<String, Long> {

    private static final long serialVersionUID = 141481953116476081L;

    @WhoCreated
    String createdBy;

    @CreatedTimestamp
    DateTime createdDate;

    @WhoModified
    String lastModifiedBy;

    @UpdatedTimestamp
    DateTime lastModifiedDate;

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Auditable#getCreatedBy()
     */
    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Auditable#setCreatedBy(java.lang.Object)
     */
    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Auditable#getCreatedDate()
     */
    @Override
    public DateTime getCreatedDate() {
        return createdDate;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Auditable#setCreatedDate(org.joda.time.DateTime)
     */
    @Override
    public void setCreatedDate(DateTime createdDate) {
        this.createdDate = createdDate;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Auditable#getLastModifiedBy()
     */
    @Override
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Auditable#setLastModifiedBy(java.lang.Object)
     */
    @Override
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Auditable#getLastModifiedDate()
     */
    @Override
    public DateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Auditable#setLastModifiedDate(org.joda.time.DateTime)
     */
    @Override
    public void setLastModifiedDate(DateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
