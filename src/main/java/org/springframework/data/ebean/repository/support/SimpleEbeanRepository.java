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

import io.ebean.EbeanServer;
import io.ebean.PagedList;
import io.ebean.SqlUpdate;
import io.ebean.UpdateQuery;
import io.ebean.text.PathProperties;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.data.ebean.repository.EbeanRepository;
import org.springframework.data.ebean.util.Converters;
import org.springframework.data.ebean.util.ExampleExpressionBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
  private static final String SELECT_FIELDS_MUST_NOT_BE_NULL = "The given select fields must not be null!";

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
  public List<T> findAll(Sort sort) {
    return db().find(getEntityType()).setOrder(Converters.convertToEbeanOrderBy(sort)).findList();
  }

  @Override
  public List<T> findAll() {
    return db().find(getEntityType()).where().findList();
  }

  @Override
  public List<T> findAllById(Iterable<ID> ids) {
    Assert.notNull(ids, "The given Iterable of Id's must not be null!");
    return db().find(getEntityType())
        .where()
        .idIn((Collection<?>) ids)
        .findList();
  }

  @Override
  public T findOne(ID id, String selects) {
    Assert.notNull(id, ID_MUST_NOT_BE_NULL);
    return db().find(getEntityType())
        .select(selects)
        .where()
        .idEq(id)
        .findOne();
  }

  @Override
  public T findOneByProperty(String propertyName, Object propertyValue) {
    Assert.notNull(propertyName, PROP_MUST_NOT_BE_NULL);
    return db().find(getEntityType())
        .where()
        .eq(propertyName, propertyValue)
        .findOne();
  }

  @Override
  public T findOneByProperty(String propertyName, Object propertyValue, String selects) {
    Assert.notNull(propertyName, PROP_MUST_NOT_BE_NULL);
    return db().find(getEntityType())
        .apply(PathProperties.parse(selects))
        .where()
        .eq(propertyName, propertyValue)
        .findOne();
  }

  @Override
  public List<T> findAll(String selects) {
    Assert.notNull(selects, SELECT_FIELDS_MUST_NOT_BE_NULL);
    return db().find(getEntityType())
        .select(selects)
        .findList();
  }

  @Override
  public List<T> findAll(Iterable<ID> ids, String selects) {
    Assert.notNull(ids, "The given Iterable of Id's must not be null!");
    Assert.notNull(selects, SELECT_FIELDS_MUST_NOT_BE_NULL);
    return db().find(getEntityType())
        .select(selects)
        .where()
        .idIn((Collection<?>) ids)
        .findList();
  }

  @Override
  public List<T> findAll(Sort sort, String selects) {
    Assert.notNull(selects, SELECT_FIELDS_MUST_NOT_BE_NULL);
    return db().find(getEntityType())
        .select(selects)
        .setOrder(Converters.convertToEbeanOrderBy(sort))
        .findList();
  }

  @Override
  public Page<T> findAll(Pageable pageable, String selects) {
    Assert.notNull(selects, SELECT_FIELDS_MUST_NOT_BE_NULL);
    PagedList<T> pagedList = db().find(getEntityType())
        .select(selects)
        .setMaxRows(pageable.getPageSize())
        .setFirstRow((int) pageable.getOffset())
        .setOrder(Converters.convertToEbeanOrderBy(pageable.getSort()))
        .findPagedList();
    return Converters.convertToSpringDataPage(pagedList, pageable.getSort());
  }

  @Override
  public <S extends T> List<S> findAll(Example<S> example) {
    return db().find(example.getProbeType())
        .where(ExampleExpressionBuilder.exampleExpression(db(), example)).findList();
  }

  @Override
  public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
    return db().find(example.getProbeType())
        .where(ExampleExpressionBuilder.exampleExpression(db(), example))
        .setOrder(Converters.convertToEbeanOrderBy(sort))
        .findList();
  }

  @Override
  public <S extends T> Optional<S> findOne(Example<S> example) {
    return db().find(example.getProbeType())
        .where(ExampleExpressionBuilder.exampleExpression(db(), example)).findOneOrEmpty();
  }

  @Override
  public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
    PagedList<S> pagedList = db().find(example.getProbeType())
        .where(ExampleExpressionBuilder.exampleExpression(db(), example))
        .setMaxRows(pageable.getPageSize())
        .setFirstRow((int) pageable.getOffset())
        .setOrder(Converters.convertToEbeanOrderBy(pageable.getSort()))
        .findPagedList();
    return Converters.convertToSpringDataPage(pagedList, pageable.getSort());
  }

  @Override
  public <S extends T> long count(Example<S> example) {
    return db().find(example.getProbeType())
        .where(ExampleExpressionBuilder.exampleExpression(db(), example)).findCount();
  }

  @Override
  public <S extends T> boolean exists(Example<S> example) {
    return db().find(example.getProbeType())
        .where(ExampleExpressionBuilder.exampleExpression(db(), example)).findCount() > 0;
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
  public Optional<T> findById(ID id) {
    Assert.notNull(id, ID_MUST_NOT_BE_NULL);
    return db().find(getEntityType()).where().idEq(id).findOneOrEmpty();
  }

  @Override
  public boolean existsById(ID id) {
    Assert.notNull(id, ID_MUST_NOT_BE_NULL);
    return db().find(getEntityType()).where().idEq(id).findCount() > 0;
  }

  @Override
  public long count() {
    return db().find(getEntityType()).findCount();
  }

  @Override
  public void deleteById(ID id) {
    Assert.notNull(id, ID_MUST_NOT_BE_NULL);
    db().delete(getEntityType(), id);
  }

  @Override
  public void delete(T t) {
    db().delete(t);
  }

  @Override
  public void deleteAll(Iterable<? extends T> entities) {
    Assert.notNull(entities, "The given Iterable of entities not be null!");
    db().deleteAll((Collection<?>) entities);
  }

  @Override
  public void deleteAll() {
    db().find(getEntityType()).delete();
  }


}
