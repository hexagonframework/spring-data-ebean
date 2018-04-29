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
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.core.support.SurroundingTransactionDetectorMethodInterceptor;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.util.Assert;

/**
 * Set of classes to contain query execution strategies. Depending (mostly) on the return type of a
 * {@link org.springframework.data.repository.query.QueryMethod} a {@link AbstractStringBasedEbeanQuery} can be executed
 * in various flavors.
 *
 * @author Xuegui Yuan
 */
public abstract class AbstractEbeanQueryExecution {

    /**
     * Executes the given {@link AbstractStringBasedEbeanQuery} with the given {@link ParameterBinder}.
     *
     * @param query  must not be {@literal null}.
     * @param values must not be {@literal null}.
     * @return
     */
    public Object execute(AbstractEbeanQuery query, Object[] values) {
        Assert.notNull(query, "AbstractEbeanQuery must not be null!");
        Assert.notNull(values, "Values must not be null!");

        return doExecute(query, values);
    }

    /**
     * Method to implement {@link AbstractStringBasedEbeanQuery} executions by single enum values.
     *
     * @param query
     * @param values
     * @return
     */
    protected abstract Object doExecute(AbstractEbeanQuery query, Object[] values);

    /**
     * Executes the query to return a simple collection of entities.
     */
    static class CollectionExecution extends AbstractEbeanQueryExecution {

        @Override
        protected Object doExecute(AbstractEbeanQuery repositoryQuery, Object[] values) {
            EbeanQueryWrapper createQuery = repositoryQuery.createQuery(values);
            return createQuery.findList();
        }
    }

    /**
     * Executes the query to return a {@link Slice} of entities.
     *
     * @author Xuegui Yuan
     */
    static class SlicedExecution extends AbstractEbeanQueryExecution {

        private final Parameters<?, ?> parameters;

        /**
         * Creates a new {@link SlicedExecution} using the given {@link Parameters}.
         *
         * @param parameters must not be {@literal null}.
         */
        public SlicedExecution(Parameters<?, ?> parameters) {
            this.parameters = parameters;
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.ebean.repository.query.AbstractEbeanQueryExecution#doExecute(org.springframework.data.ebean.repository.query.AbstractEbeanQuery, java.lang.Object[])
         */
        @Override
        @SuppressWarnings("unchecked")
        protected Object doExecute(AbstractEbeanQuery query, Object[] values) {
            ParametersParameterAccessor accessor = new ParametersParameterAccessor(parameters, values);
            EbeanQueryWrapper createQuery = query.createQuery(values);
            return createQuery.findSlice(accessor.getPageable());
        }
    }

    /**
     * Executes the {@link AbstractStringBasedEbeanQuery} to return a {@link org.springframework.data.domain.Page} of
     * entities.
     */
    static class PagedExecution extends AbstractEbeanQueryExecution {

        private final Parameters<?, ?> parameters;

        public PagedExecution(Parameters<?, ?> parameters) {

            this.parameters = parameters;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Object doExecute(final AbstractEbeanQuery repositoryQuery, final Object[] values) {
            ParameterAccessor accessor = new ParametersParameterAccessor(parameters, values);
            EbeanQueryWrapper createQuery = repositoryQuery.createQuery(values);
            return createQuery.findPage(accessor.getPageable());
        }
    }


    /**
     * Executes a {@link AbstractStringBasedEbeanQuery} to return a single entity.
     */
    static class SingleEntityExecution extends AbstractEbeanQueryExecution {

        @Override
        protected Object doExecute(AbstractEbeanQuery query, Object[] values) {
            EbeanQueryWrapper createQuery = query.createQuery(values);
            return createQuery.findOne();
        }
    }

    /**
     * Executes a update query such as an update, insert or delete.
     */
    static class UpdateExecution extends AbstractEbeanQueryExecution {

        private final EbeanServer ebeanServer;

        /**
         * Creates an execution that automatically clears the given {@link EbeanServer} after execution if the given
         * {@link EbeanServer} is not {@literal null}.
         *
         * @param ebeanServer
         */
        public UpdateExecution(EbeanQueryMethod method, EbeanServer ebeanServer) {

            Class<?> returnType = method.getReturnType();

            boolean isVoid = void.class.equals(returnType) || Void.class.equals(returnType);
            boolean isInt = int.class.equals(returnType) || Integer.class.equals(returnType);

            Assert.isTrue(isInt || isVoid, "Modifying queries can only use void or int/Integer as return type!");

            this.ebeanServer = ebeanServer;
        }

        @Override
        protected Object doExecute(AbstractEbeanQuery query, Object[] values) {
            EbeanQueryWrapper createQuery = query.createQuery(values);
            return createQuery.update();
        }
    }

    /**
     * {@link AbstractEbeanQueryExecution} removing entities matching the query.
     *
     * @author Xuegui Yuan
     */
    static class DeleteExecution extends AbstractEbeanQueryExecution {

        private final EbeanServer ebeanServer;

        public DeleteExecution(EbeanServer ebeanServer) {
            this.ebeanServer = ebeanServer;
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.ebean.repository.query.AbstractEbeanQueryExecution#doExecute(org.springframework.data.ebean.repository.query.AbstractEbeanQuery, java.lang.Object[])
         */
        @Override
        protected Object doExecute(AbstractEbeanQuery ebeanQuery, Object[] values) {
            EbeanQueryWrapper createQuery = ebeanQuery.createQuery(values);
            return createQuery.delete();
        }
    }

    /**
     * {@link AbstractEbeanQueryExecution} performing an exists check on the query.
     *
     * @author Xuegui Yuan
     */
    static class ExistsExecution extends AbstractEbeanQueryExecution {

        @Override
        protected Object doExecute(AbstractEbeanQuery ebeanQuery, Object[] values) {
            EbeanQueryWrapper createQuery = ebeanQuery.createQuery(values);
            return createQuery.isExists();
        }
    }

    /**
     * {@link AbstractEbeanQueryExecution} executing a Java 8 Stream.
     *
     * @author Xuegui Yuan
     */
    static class StreamExecution extends AbstractEbeanQueryExecution {

        private static final String NO_SURROUNDING_TRANSACTION = "You're trying to execute a streaming query method without a surrounding transaction that keeps the connection open so that the Stream can actually be consumed. Make sure the code consuming the stream uses @Transactional or any other way of declaring a (read-only) transaction.";

        /*
         * (non-Javadoc)
         * @see org.springframework.data.ebean.repository.query.AbstractEbeanQueryExecution#doExecute(org.springframework.data.ebean.repository.query.AbstractEbeanQuery, java.lang.Object[])
         */
        @Override
        protected Object doExecute(final AbstractEbeanQuery ebeanQuery, Object[] values) {
            if (!SurroundingTransactionDetectorMethodInterceptor.INSTANCE.isSurroundingTransactionActive()) {
                throw new InvalidDataAccessApiUsageException(NO_SURROUNDING_TRANSACTION);
            }

            EbeanQueryWrapper createQuery = ebeanQuery.createQuery(values);
            return createQuery.findStream();
        }
    }
}
