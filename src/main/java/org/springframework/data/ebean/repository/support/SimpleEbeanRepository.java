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

package org.springframework.data.ebean.repository.support;

import io.ebean.*;
import io.ebean.text.PathProperties;
import org.springframework.data.domain.*;
import org.springframework.data.ebean.repository.EbeanRepository;
import org.springframework.data.ebean.util.Converters;
import org.springframework.data.ebean.util.ExampleExpressionBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of the {@link org.springframework.data.repository.CrudRepository} interface. This will offer
 * you a more sophisticated interface than the plain {@link io.ebean.EbeanServer} .
 *
 * @param <T>  the type of the entity to handle
 * @param <ID> the type of the entity's identifier
 * @author Xuegui Yuan
 */
@Repository
@Transactional(rollbackFor = Exception.class)
public class SimpleEbeanRepository<T extends Persistable, ID extends Serializable> implements EbeanRepository<T, ID> {

    private static final String ID_MUST_NOT_BE_NULL = "The given id must not be null!";
    private static final String PROP_MUST_NOT_BE_NULL = "The given property must not be null!";

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
    public Page<T> findAll(Pageable pageable) {
        PagedList<T> pagedList = db().find(getEntityType())
                .setMaxRows(pageable.getPageSize())
                .setFirstRow((int) pageable.getOffset())
                .setOrder(Converters.convertToEbeanOrderBy(pageable.getSort()))
                .findPagedList();
        return Converters.convertToSpringDataPage(pagedList, pageable.getSort());
    }

    @Override
    public EbeanServer db() {
        return ebeanServer;
    }

    private Class<T> getEntityType() {
        return entityType;
    }

    @Override
    public EbeanServer db(EbeanServer db) {
        this.ebeanServer = db;
        return this.ebeanServer;
    }

    @Override
    public UpdateQuery<T> updateQuery() {
        return db().update(getEntityType());
    }

    @Override
    public SqlUpdate sqlUpdateOf(String sql) {
        return db().createSqlUpdate(sql);
    }

    @Override
    public <S extends T> S save(S s) {
        db().save(s);
        return s;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        Assert.notNull(entities, "The given Iterable of entities not be null!");
        db().saveAll((Collection<?>) entities);
        return entities;
    }

    @Override
    public <S extends T> S update(S s) {
        db().update(s);
        return s;
    }

    @Override
    public Iterable<T> updateAll(Iterable<T> entities) {
        Assert.notNull(entities, "The given Iterable of entities not be null!");
        db().updateAll((Collection<?>) entities);
        return entities;
    }

    @Override
    public void deleteById(ID id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        db().delete(getEntityType(), id);
    }

    @Override
    public void deletePermanentById(ID id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        db().deletePermanent(getEntityType(), id);
    }

    @Override
    public void delete(T t) {
        db().delete(t);
    }

    @Override
    public void deletePermanent(T t) {
        db().deletePermanent(t);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        Assert.notNull(entities, "The given Iterable of entities not be null!");
        db().deleteAll((Collection<?>) entities);
    }

    @Override
    public void deletePermanentAll(Iterable<? extends T> entities) {
        Assert.notNull(entities, "The given Iterable of entities not be null!");
        db().deleteAllPermanent((Collection<?>) entities);
    }

    @Override
    public void deleteAll() {
        query().delete();
    }

    @Override
    public void deletePermanentAll() {
        query().setIncludeSoftDeletes().delete();
    }

    @Override
    public Optional<T> findById(ID id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return query().where().idEq(id).findOneOrEmpty();
    }

    @Override
    public Optional<T> findById(String fetchPath, ID id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return query(fetchPath)
                .where()
                .idEq(id)
                .findOneOrEmpty();
    }

    @Override
    public Optional<T> findByProperty(String propertyName, Object propertyValue) {
        Assert.notNull(propertyName, PROP_MUST_NOT_BE_NULL);
        return query()
                .where()
                .eq(propertyName, propertyValue)
                .findOneOrEmpty();
    }

    @Override
    public Optional<T> findByProperty(String fetchPath, String propertyName, Object propertyValue) {
        Assert.notNull(propertyName, PROP_MUST_NOT_BE_NULL);
        return query(fetchPath)
                .where()
                .eq(propertyName, propertyValue)
                .findOneOrEmpty();
    }

    @Override
    public List<T> findAllByProperty(String propertyName, Object propertyValue) {
        return query()
                .where()
                .eq(propertyName, propertyValue)
                .findList();
    }

    @Override
    public List<T> findAllByProperty(String fetchPath, String propertyName, Object propertyValue) {
        return query(fetchPath)
                .where()
                .eq(propertyName, propertyValue)
                .findList();
    }

    @Override
    public List<T> findAllByProperty(String fetchPath, String propertyName, Object propertyValue, Sort sort) {
        return query(fetchPath, sort)
                .where()
                .eq(propertyName, propertyValue)
                .findList();
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        Assert.notNull(ids, "The given Iterable of Id's must not be null!");
        return query()
                .where()
                .idIn((Collection<?>) ids)
                .findList();
    }

    @Override
    public List<T> findAll() {
        return query()
                .findList();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return query()
                .setOrder(Converters.convertToEbeanOrderBy(sort))
                .findList();
    }

    @Override
    public List<T> findAll(String fetchPath) {
        return query(fetchPath)
                .findList();
    }

    @Override
    public List<T> findAll(String fetchPath, Iterable<ID> ids) {
        Assert.notNull(ids, "The given Iterable of Id's must not be null!");
        return query(fetchPath)
                .where()
                .idIn((Collection<?>) ids)
                .findList();
    }

    @Override
    public List<T> findAll(String fetchPath, Sort sort) {
        return query(fetchPath, sort)
                .findList();
    }

    @Override
    public Page<T> findAll(String fetchPath, Pageable pageable) {
        PagedList<T> pagedList = query(fetchPath)
                .setMaxRows(pageable.getPageSize())
                .setFirstRow((int) pageable.getOffset())
                .setOrder(Converters.convertToEbeanOrderBy(pageable.getSort()))
                .findPagedList();
        return Converters.convertToSpringDataPage(pagedList, pageable.getSort());
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        return queryByExample(example).findList();
    }

    @Override
    public <S extends T> List<S> findAll(String fetchPath, Example<S> example) {
        return queryByExample(fetchPath, example)
                .findList();
    }

    @Override
    public <S extends T> List<S> findAll(String fetchPath, Example<S> example, Sort sort) {
        return queryByExample(fetchPath, example, sort)
                .findList();
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        return queryByExample(null, example, sort)
                .findList();
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        return queryByExample(example).findOneOrEmpty();
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        PagedList<S> pagedList = queryByExample(example)
                .setMaxRows(pageable.getPageSize())
                .setFirstRow((int) pageable.getOffset())
                .setOrder(Converters.convertToEbeanOrderBy(pageable.getSort()))
                .findPagedList();
        return Converters.convertToSpringDataPage(pagedList, pageable.getSort());
    }

    @Override
    public <S extends T> Page<S> findAll(String fetchPath, Example<S> example, Pageable pageable) {
        PagedList<S> pagedList = queryByExample(fetchPath, example)
                .setMaxRows(pageable.getPageSize())
                .setFirstRow((int) pageable.getOffset())
                .setOrder(Converters.convertToEbeanOrderBy(pageable.getSort()))
                .findPagedList();
        return Converters.convertToSpringDataPage(pagedList, pageable.getSort());
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        return queryByExample(example).findCount();
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        return queryByExample(example).findCount() > 0;
    }

    @Override
    public boolean existsById(ID id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return query().where().idEq(id).findCount() > 0;
    }

    @Override
    public long count() {
        return query().findCount();
    }

    private Query<T> query() {
        return db().find(getEntityType());
    }

    private Query<T> query(String fetchPath) {
        Query<T> query = query();
        if (StringUtils.hasText(fetchPath)) {
            query.apply(PathProperties.parse(fetchPath));
        }
        return query;
    }

    private Query<T> query(String fetchPath, Sort sort) {
        if (sort == null) {
            return query(fetchPath);
        } else {
            return query(fetchPath).setOrder(Converters.convertToEbeanOrderBy(sort));
        }
    }

    private <S extends T> Query<S> queryByExample(Example<S> example) {
        return db().find(example.getProbeType()).where(ExampleExpressionBuilder.exampleExpression(db(), example));
    }

    private <S extends T> Query<S> queryByExample(String fetchPath, Example<S> example) {
        Query<S> query = queryByExample(example);
        if (StringUtils.hasText(fetchPath)) {
            query.apply(PathProperties.parse(fetchPath));
        }
        return query;
    }

    private <S extends T> Query<S> queryByExample(String fetchPath, Example<S> example, Sort sort) {
        Query<S> query = queryByExample(fetchPath, example);
        if (sort != null) {
            query.setOrder(Converters.convertToEbeanOrderBy(sort));
        }
        return query;
    }

}
