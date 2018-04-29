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

package org.springframework.data.ebean.repository.query;

import io.ebean.EbeanServer;
import org.springframework.data.ebean.annotation.Query;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

/**
 * EbeanQueryWrapper lookup strategy to execute finders.
 *
 * @author Xuegui Yuan
 */
public final class EbeanQueryLookupStrategy {

    /**
     * Private constructor to prevent instantiation.
     */
    private EbeanQueryLookupStrategy() {
    }

    /**
     * Creates a {@link QueryLookupStrategy} for the given {@link EbeanServer} and {@link Key}.
     *
     * @param ebeanServer               must not be {@literal null}.
     * @param key                       may be {@literal null}.
     * @param evaluationContextProvider must not be {@literal null}.
     * @return
     */
    public static QueryLookupStrategy create(EbeanServer ebeanServer, Key key,
                                             EvaluationContextProvider evaluationContextProvider) {

        Assert.notNull(ebeanServer, "EbeanServer must not be null!");
        Assert.notNull(evaluationContextProvider, "EvaluationContextProvider must not be null!");

        switch (key != null ? key : Key.CREATE_IF_NOT_FOUND) {
            case CREATE:
                return new CreateQueryLookupStrategy(ebeanServer);
            case USE_DECLARED_QUERY:
                return new DeclaredQueryLookupStrategy(ebeanServer, evaluationContextProvider);
            case CREATE_IF_NOT_FOUND:
                return new CreateIfNotFoundQueryLookupStrategy(ebeanServer, new CreateQueryLookupStrategy(ebeanServer),
                        new DeclaredQueryLookupStrategy(ebeanServer, evaluationContextProvider));
            default:
                throw new IllegalArgumentException(String.format("Unsupported query lookup strategy %s!", key));
        }
    }

    /**
     * {@link QueryLookupStrategy} to create a query from the method name.
     *
     * @author Xuegui Yuan
     */
    private static class CreateQueryLookupStrategy extends AbstractQueryLookupStrategy {

        public CreateQueryLookupStrategy(EbeanServer ebeanServer) {

            super(ebeanServer);
        }

        @Override
        protected RepositoryQuery resolveQuery(EbeanQueryMethod method, EbeanServer ebeanServer, NamedQueries namedQueries) {

            try {
                return new PartTreeEbeanQuery(method, ebeanServer);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        String.format("Could not create query metamodel for method %s!", method.toString()), e);
            }
        }

    }

    /**
     * Base class for {@link QueryLookupStrategy} implementations that need access to an {@link EbeanServer}.
     *
     * @author Oliver Gierke
     * @author Thomas Darimont
     */
    private abstract static class AbstractQueryLookupStrategy implements QueryLookupStrategy {

        private final EbeanServer ebeanServer;

        /**
         * Creates a new {@link AbstractQueryLookupStrategy}.
         *
         * @param ebeanServer
         */
        public AbstractQueryLookupStrategy(EbeanServer ebeanServer) {
            this.ebeanServer = ebeanServer;
        }

        @Override
        public final RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory,
                                                  NamedQueries namedQueries) {
            return resolveQuery(new EbeanQueryMethod(method, metadata, factory), ebeanServer, namedQueries);
        }

        /**
         * Resolve query to return .RepositoryQuery
         *
         * @param method
         * @param ebeanServer
         * @param namedQueries
         * @return RepositoryQuery
         */
        protected abstract RepositoryQuery resolveQuery(EbeanQueryMethod method, EbeanServer ebeanServer, NamedQueries namedQueries);
    }

    /**
     * {@link QueryLookupStrategy} to try to detect a declared query first (
     * {@link Query}, ebean named query). In case none is found we fall back on
     * query creation.
     *
     * @author Oliver Gierke
     * @author Thomas Darimont
     */
    private static class CreateIfNotFoundQueryLookupStrategy extends AbstractQueryLookupStrategy {

        private final DeclaredQueryLookupStrategy lookupStrategy;
        private final CreateQueryLookupStrategy createStrategy;

        /**
         * Creates a new {@link CreateIfNotFoundQueryLookupStrategy}.
         *
         * @param ebeanServer
         * @param createStrategy
         * @param lookupStrategy
         */
        public CreateIfNotFoundQueryLookupStrategy(EbeanServer ebeanServer,
                                                   CreateQueryLookupStrategy createStrategy, DeclaredQueryLookupStrategy lookupStrategy) {
            super(ebeanServer);

            this.createStrategy = createStrategy;
            this.lookupStrategy = lookupStrategy;
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.ebean.repository.query.ebeanQueryLookupStrategy.AbstractQueryLookupStrategy#resolveQuery(org.springframework.data.ebean.repository.query.ebeanQueryMethod, javax.persistence.EntityManager, org.springframework.data.repository.core.NamedQueries)
         */
        @Override
        protected RepositoryQuery resolveQuery(EbeanQueryMethod method, EbeanServer ebeanServer, NamedQueries namedQueries) {
            try {
                return lookupStrategy.resolveQuery(method, ebeanServer, namedQueries);
            } catch (IllegalStateException e) {
                return createStrategy.resolveQuery(method, ebeanServer, namedQueries);
            }
        }
    }

    /**
     * {@link QueryLookupStrategy} that tries to detect a declared query declared via {@link io.ebean.Query} annotation followed by
     * a ebean named query lookup.
     *
     * @author Oliver Gierke
     * @author Thomas Darimont
     */
    private static class DeclaredQueryLookupStrategy extends AbstractQueryLookupStrategy {

        private final EvaluationContextProvider evaluationContextProvider;

        /**
         * Creates a new {@link DeclaredQueryLookupStrategy}.
         *
         * @param ebeanServer
         * @param evaluationContextProvider
         */
        public DeclaredQueryLookupStrategy(EbeanServer ebeanServer,
                                           EvaluationContextProvider evaluationContextProvider) {
            super(ebeanServer);
            this.evaluationContextProvider = evaluationContextProvider;
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.ebean.repository.query.ebeanQueryLookupStrategy.AbstractQueryLookupStrategy#resolveQuery(org.springframework.data.ebean.repository.query.ebeanQueryMethod, javax.persistence.EntityManager, org.springframework.data.repository.core.NamedQueries)
         */
        @Override
        protected RepositoryQuery resolveQuery(EbeanQueryMethod method, EbeanServer ebeanServer, NamedQueries namedQueries) {
            RepositoryQuery query = EbeanQueryFactory.INSTANCE.fromQueryAnnotation(method, ebeanServer, evaluationContextProvider);

            if (null != query) {
                return query;
            }

            String name = method.getNamedQueryName();
            if (namedQueries.hasQuery(name)) {
                return EbeanQueryFactory.INSTANCE.fromMethodWithQueryString(method, ebeanServer, namedQueries.getQuery(name),
                        evaluationContextProvider);
            }

            query = NamedEbeanQuery.lookupFrom(method, ebeanServer);

            if (null != query) {
                return query;
            }

            throw new IllegalStateException(
                    String.format("Did neither find a NamedQuery nor an annotated query for method %s!", method));
        }
    }
}
