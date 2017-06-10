/*
 * Copyright 2008-2016 the original author or authors.
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

import io.ebean.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;

/**
 * Ebean specific extension of {@link org.springframework.data.repository.Repository}.
 *
 * @author Xuegui Yuan
 */
@NoRepositoryBean
public interface EbeanRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {

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
     * Return an object relational query for finding a List, Set, Map or single entity bean.
     *
     * @return the Query.
     */
    Query<T> query();

    /**
     * Return a query using OQL.
     *
     * @param oql the Ebean ORM query
     * @return the created Query using ORM query
     */
    Query<T> queryWithOql(String oql);

    /**
     * Return a query using native SQL.
     *
     * @param sql native SQL
     * @return the created Query using native SQL
     */
    Query<T> queryWithSql(String sql);

    /**
     * Return a query using query name.
     *
     * @param queryName
     * @return
     */
    Query<T> namedQuery(String queryName);

    /**
     * Return an SqlQuery for performing native SQL queries that return SqlRow's.
     *
     * @param sql the sql to create SqlQuery using native SQL
     * @return the created SqlQuery.
     */
    SqlQuery sqlQuery(String sql);

    /**
     * Return an UpdateQuery to perform a bulk update of many rows that match the query.
     *
     * @return the created UpdateQuery.
     */
    UpdateQuery<T> updateQuery();

    /**
     * Return a SqlUpdate for executing insert update or delete statements.
     *
     * @return the created SqlUpdate using native SQL
     */
    SqlUpdate sqlUpdate(String sql);

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findAll()
     */
    List<T> findAll();

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Sort)
     */
    List<T> findAll(Sort sort);

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findAll(java.lang.Iterable)
     */
    List<T> findAll(Iterable<ID> ids);

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#save(java.lang.Iterable)
     */
    <S extends T> List<S> save(Iterable<S> entities);

    /**
     * Retrieves an entity by its id and select return entity properties with FetchPath string.
     * @param id ID
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
     * @param selects FetchPath string
     * @return all entities only select/fetch with FetchPath string
     */
    List<T> findAll(String selects);

    /**
     * Returns all entities in ids and select return entity properties with FetchPath string.
     * @param ids ID list
     * @param selects FetchPath string
     * @return all entities by id in ids and select/fetch with FetchPath string
     */
    List<T> findAll(Iterable<ID> ids, String selects);

    /**
     * Returns all entities sorted by the given options and select return entity properties with FetchPath string.
     * @param sort
     * @param selects
     * @return all entities sorted and select/fetch with FetchPath string
     */
    List<T> findAll(Sort sort, String selects);

    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
     * and select return entity properties with FetchPath string.
     * @param pageable
     * @param selects
     * @return a page of entities select/fetch with FetchPath string
     */
    Page<T> findAll(Pageable pageable, String selects);

}
