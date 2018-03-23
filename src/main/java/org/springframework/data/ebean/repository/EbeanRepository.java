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

package org.springframework.data.ebean.repository;

import io.ebean.EbeanServer;
import io.ebean.SqlUpdate;
import io.ebean.UpdateQuery;
import java.io.Serializable;
import java.util.List;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

/**
 * Ebean specific extension of {@link org.springframework.data.repository.Repository}.
 *
 * @author Xuegui Yuan
 */
@NoRepositoryBean
public interface EbeanRepository<T extends Persistable, ID extends Serializable> extends PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> {

  /**
   * Return the current EbeanServer.
   *
   * @return the current EbeanServer
   */
  EbeanServer db();

  /**
   * Set the current EbeanServer.
   *
   * @param db current EbeanServer
   * @return the current EbeanServer
   */
  EbeanServer db(EbeanServer db);

  /**
   * Return an UpdateQuery to perform a bulk update of many rows that match the query.
   *
   * @return the created UpdateQuery.
   */
  UpdateQuery<T> updateQuery();

  /**
   * Return a SqlUpdate for executing insert update or delete statements.
   *
   * @param sql native SQL
   * @return the created SqlUpdate using native SQL
   */
  SqlUpdate sqlUpdateOf(String sql);

  /**
   * Update entity which is not loaded.
   *
   * @param s
   * @param <S>
   * @return
   */
  <S extends T> S update(S s);

  /**
   * Update entities which is not loaded.
   *
   * @param entities
   * @return
   */
  Iterable<T> updateAll(Iterable<T> entities);

  /**
   * Find all order by sort config.
   *
   * @param sort Order by
   * @return List
   */
  @Override
  List<T> findAll(Sort sort);

  /**
   * Find All.
   *
   * @return List
   */
  @Override
  List<T> findAll();

  /**
   * Find all by id list.
   *
   * @param ids Id list
   * @return List
   */
  @Override
  List<T> findAllById(Iterable<ID> ids);

  /**
   * Retrieves an entity by its id and select return entity properties with FetchPath string.
   *
   * @param id      ID
   * @param selects FetchPath string
   * @return the entity only select/fetch with FetchPath string with the given id or {@literal null} if none found
   */
  T findOne(ID id, String selects);

  /**
   * Retrieves an entity by its property name value.
   *
   * @param propertyName  property name
   * @param propertyValue property value
   * @return the entity with the given property name value or {@literal null} if none found
   */
  T findOneByProperty(String propertyName, Object propertyValue);

  /**
   * Retrieves an entity by its property name value and select return entity properties with FetchPath string.
   *
   * @param propertyName  property name
   * @param propertyValue property value
   * @param selects       FetchPath string
   * @return the entity only select/fetch with FetchPath string with the given property name value or {@literal null} if none found
   */
  T findOneByProperty(String propertyName, Object propertyValue, String selects);

  /**
   * Returns all entities and select return entity properties with FetchPath string.
   *
   * @param selects FetchPath string
   * @return all entities only select/fetch with FetchPath string
   */
  List<T> findAll(String selects);

  /**
   * Returns all entities in ids and select return entity properties with FetchPath string.
   *
   * @param ids     ID list
   * @param selects FetchPath string
   * @return all entities by id in ids and select/fetch with FetchPath string
   */
  List<T> findAll(Iterable<ID> ids, String selects);

  /**
   * Returns all entities sorted by the given options and select return entity properties with FetchPath string.
   *
   * @param sort
   * @param selects
   * @return all entities sorted and select/fetch with FetchPath string
   */
  List<T> findAll(Sort sort, String selects);

  /**
   * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
   * and select return entity properties with FetchPath string.
   *
   * @param pageable
   * @param selects
   * @return a page of entities select/fetch with FetchPath string
   */
  Page<T> findAll(Pageable pageable, String selects);

  /**
   * Returns all entities matching the given {@link Example}. In case no match could be found an empty {@link Iterable}
   * is returned.
   *
   * @param example must not be {@literal null}.
   * @return all entities matching the given {@link Example}.
   */
  @Override
  <S extends T> List<S> findAll(Example<S> example);

  /**
   * Returns all entities matching the given {@link Example} applying the given {@link Sort}. In case no match could be
   * found an empty {@link Iterable} is returned.
   *
   * @param example must not be {@literal null}.
   * @param sort    the {@link Sort} specification to sort the results by, must not be {@literal null}.
   * @return all entities matching the given {@link Example}.
   */
  @Override
  <S extends T> List<S> findAll(Example<S> example, Sort sort);
}
