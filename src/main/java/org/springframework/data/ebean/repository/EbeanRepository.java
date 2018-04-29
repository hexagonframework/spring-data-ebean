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
import org.springframework.data.domain.*;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

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
     * @return the created UpdateQuery
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
     * @param s   entity to update
     * @param <S> entity extends T
     * @return entity Updated entity
     */
    <S extends T> S update(S s);

    /**
     * Update entities which is not loaded.
     *
     * @param entities entities to update
     * @return entities Updated entities list
     */
    Iterable<T> updateAll(Iterable<T> entities);

    /**
     * Deletes the entity permanent with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@code id} is {@literal null}
     */
    void deletePermanentById(ID id);

    /**
     * Deletes a given entity permanent.
     *
     * @param entity the entity to be deleted
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    void deletePermanent(T entity);

    /**
     * Deletes the given entities permanent.
     *
     * @param entities the entities to be deleted
     * @throws IllegalArgumentException in case the given {@link Iterable} is {@literal null}.
     */
    void deletePermanentAll(Iterable<? extends T> entities);

    /**
     * Deletes all entities permanent managed by the repository.
     */
    void deletePermanentAll();

    /**
     * Retrieves an entity by its id and select return entity properties with FetchPath string.
     *
     * @param fetchPath FetchPath string
     * @param id        ID
     * @return the entity only select/fetch with FetchPath string with the given id or {@literal null} if none found
     */
    Optional<T> findById(String fetchPath, ID id);

    /**
     * Retrieves an entity by its property name value.
     *
     * @param propertyName  property name
     * @param propertyValue property value
     * @return the entity with the given property name value or {@literal null} if none found
     */
    Optional<T> findByProperty(String propertyName, Object propertyValue);

    /**
     * Retrieves an entity by its property name value and select return entity properties with FetchPath string.
     *
     * @param fetchPath     FetchPath string
     * @param propertyName  property name
     * @param propertyValue property value
     * @return the entity only select/fetch with FetchPath string with the given property name value or {@literal null} if none found
     */
    Optional<T> findByProperty(String fetchPath, String propertyName, Object propertyValue);

    /**
     * Retrieves an entities by its property name value.
     *
     * @param propertyName  property name
     * @param propertyValue property value
     * @return the entity with the given property name value or {@literal null} if none found
     */
    List<T> findAllByProperty(String propertyName, Object propertyValue);

    /**
     * Retrieves all entities by its property name value and select return entity properties with FetchPath string.
     *
     * @param fetchPath     FetchPath string
     * @param propertyName  property name
     * @param propertyValue property value
     * @return the entity only select/fetch with FetchPath string with the given property name value or {@literal null} if none found
     */
    List<T> findAllByProperty(String fetchPath, String propertyName, Object propertyValue);

    /**
     * Retrieves an entity by its property name value and select return entity properties with FetchPath string.
     *
     * @param fetchPath     FetchPath string.
     * @param propertyName  property name.
     * @param propertyValue property value.
     * @param sort          order by.
     * @return the entity only select/fetch with FetchPath string with the given property name value or {@literal null} if none found.
     */
    List<T> findAllByProperty(String fetchPath, String propertyName, Object propertyValue, Sort sort);

    /**
     * Find all by id list.
     *
     * @param ids id list.
     * @return List all entities list.
     */
    @Override
    List<T> findAllById(Iterable<ID> ids);

    /**
     * Find All.
     *
     * @return List all entities list.
     */
    @Override
    List<T> findAll();

    /**
     * Find all order by sort config.
     *
     * @param sort order by.
     * @return List all entities list.
     */
    @Override
    List<T> findAll(Sort sort);

    /**
     * Returns all entities and select return entity properties with FetchPath string.
     *
     * @param fetchPath FetchPath string.
     * @return all entities only select/fetch with FetchPath string.
     */
    List<T> findAll(String fetchPath);

    /**
     * Returns all entities in ids and select return entity properties with FetchPath string.
     *
     * @param fetchPath FetchPath string.
     * @param ids       ID list.
     * @return all entities by id in ids and select/fetch with FetchPath string.
     */
    List<T> findAll(String fetchPath, Iterable<ID> ids);

    /**
     * Returns all entities sorted by the given options and select return entity properties with FetchPath string.
     *
     * @param fetchPath FetchPath string.
     * @param sort      order by.
     * @return all entities sorted and select/fetch with FetchPath string.
     */
    List<T> findAll(String fetchPath, Sort sort);

    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
     * and select return entity properties with FetchPath string.
     *
     * @param fetchPath FetchPath string.
     * @param pageable  page request.
     * @return a page of entities select/fetch with FetchPath string.
     */
    Page<T> findAll(String fetchPath, Pageable pageable);

    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
     * and matching the given {@link Example} and select return entity properties with FetchPath string.
     *
     * @param fetchPath FetchPath string.
     * @param example   must not be {@literal null}.
     * @param pageable  page request.
     * @return a page of entities select/fetch with FetchPath string.
     */
    <S extends T> Page<S> findAll(String fetchPath, Example<S> example, Pageable pageable);

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
     * @param fetchPath FetchPath string.
     * @param example   must not be {@literal null}.
     * @return all entities matching the given {@link Example}.
     */
    <S extends T> List<S> findAll(String fetchPath, Example<S> example);

    /**
     * Returns all entities matching the given {@link Example} applying the given {@link Sort}. In case no match could be
     * found an empty {@link Iterable} is returned.
     *
     * @param fetchPath FetchPath string.
     * @param example   must not be {@literal null}.
     * @param sort      the {@link Sort} specification to sort the results by, must not be {@literal null}.
     * @return all entities matching the given {@link Example}.
     */
    <S extends T> List<S> findAll(String fetchPath, Example<S> example, Sort sort);

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
