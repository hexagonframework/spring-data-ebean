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
package org.springframework.data.ebean.repository.support;

import io.ebean.EbeanServer;
import io.ebean.OrderBy;
import io.ebean.PagedList;
import org.springframework.data.domain.*;
import org.springframework.data.ebean.repository.EbeanRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Default implementation of the {@link org.springframework.data.repository.CrudRepository} interface. This will offer
 * you a more sophisticated interface than the plain {@link io.ebean.EbeanServer} .
 *
 * @param <T>  the type of the entity to handle
 * @param <ID> the type of the entity's identifier
 * @author Xuegui Yuan
 */
@Repository
@Transactional(readOnly = true)
public class SimpleEbeanRepository<T, ID extends Serializable> implements EbeanRepository<T, ID> {

    private static final String ID_MUST_NOT_BE_NULL = "The given id must not be null!";

    private EbeanServer ebeanServer;

    private Class<T> entityType;


    /**
     * Creates a new {@link SimpleEbeanRepository} to manage objects of the given domain type.
     *
     * @param entityType  must not be {@literal null}.
     * @param ebeanServer must not be {@literal null}.
     */
    public SimpleEbeanRepository(Class<T> entityType, EbeanServer ebeanServer) {
        this.entityType = entityType;
        this.ebeanServer = ebeanServer;
    }

    @Override
    public EbeanServer db() {
        return ebeanServer;
    }

    @Override
    public EbeanServer db(EbeanServer db) {
        this.ebeanServer = db;
        return this.ebeanServer;
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<T> entityType) {
        this.entityType = entityType;
    }

    @Override
    public <S extends T> S save(S s) {
        db().save(s);
        return s;
    }

    @Override
    public <S extends T> List<S> save(Iterable<S> entities) {
        List<S> result = new ArrayList<S>();

        if (entities == null) {
            return result;
        }

        for (S entity : entities) {
            result.add(save(entity));
        }

        return result;
    }

    @Override
    public T findOne(ID id) {
        return db().find(getEntityType()).where().idEq(id).findUnique();
    }

    @Override
    public boolean exists(ID id) {
        return db().find(getEntityType()).where().idEq(id).findCount() > 0;
    }

    @Override
    public List<T> findAll() {
        return db().find(getEntityType()).where().findList();
    }

    @Override
    public List<T> findAll(Iterable<ID> ids) {
        return db().find(getEntityType()).where().idIn(ids).findList();
    }

    @Override
    public long count() {
        return db().find(getEntityType()).findCount();
    }

    @Override
    public void delete(ID id) {
        db().find(getEntityType()).where().idEq(id).delete();
    }

    @Override
    public void delete(T t) {
        db().delete(t);
    }

    @Override
    public void delete(Iterable<? extends T> iterable) {
        db().deleteAll((Collection<?>) iterable);
    }

    @Override
    public void deleteAll() {
        db().find(getEntityType()).delete();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return db().find(getEntityType()).setOrder(ebeanOrderFromSpringDataSort(sort)).findList();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return springDataPageFromEbeanPageList(db().find(getEntityType()).setMaxRows(pageable.getPageSize()).setFirstRow(pageable.getOffset()).findPagedList());
    }

    protected OrderBy<T> ebeanOrderFromSpringDataSort(Sort sort) {
        List<String> list = new ArrayList<>();
        while (sort.iterator().hasNext()) {
            Sort.Order so = sort.iterator().next();
            list.add(so.getDirection() == Sort.Direction.ASC ? so.getProperty() + " asc" : so.getProperty() + " desc");
        }
        return new OrderBy<T>(StringUtils.collectionToCommaDelimitedString(list));
    }

    protected Page<T> springDataPageFromEbeanPageList(PagedList pagedList) {
        return new PageImpl<T>(pagedList.getList(),
                new PageRequest(pagedList.getPageIndex(), pagedList.getPageSize()),
                pagedList.getTotalCount());
    }

    @Override
    public T findOne(ID id, String selects) {
        return db().find(getEntityType()).select(selects).where().idEq(id).findUnique();
    }

    @Override
    public List<T> findAll(String selects) {
        return db().find(getEntityType()).select(selects).findList();
    }

    @Override
    public List<T> findAll(Iterable<ID> ids, String selects) {
        return db().find(getEntityType()).select(selects).where().idIn(ids).findList();
    }

    @Override
    public List<T> findAll(Sort sort, String selects) {
        return db().find(getEntityType()).select(selects).setOrder(ebeanOrderFromSpringDataSort(sort)).findList();
    }

    @Override
    public Page<T> findAll(Pageable pageable, String selects) {
        return springDataPageFromEbeanPageList(db().find(getEntityType()).select(selects).setMaxRows(pageable.getPageSize()).setFirstRow(pageable.getOffset()).findPagedList());
    }

}
