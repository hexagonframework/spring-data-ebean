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
import io.ebean.ExpressionList;
import io.ebean.Query;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.DefaultParameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.ResultProcessor;
import org.springframework.data.repository.query.parser.PartTree;

/**
 * A {@link AbstractEbeanQuery} implementation based on a {@link PartTree}.
 *
 * @author Xuegui Yuan
 */
public class PartTreeEbeanQuery extends AbstractEbeanQuery {

    private final Class<?> domainClass;
    private final PartTree tree;
    private final DefaultParameters parameters;

    private final QueryPreparer queryPreparer;

    /**
     * Creates a new {@link PartTreeEbeanQuery}.
     *
     * @param method      must not be {@literal null}.
     * @param ebeanServer must not be {@literal null}.
     */
    public PartTreeEbeanQuery(EbeanQueryMethod method, EbeanServer ebeanServer) {
        super(method, ebeanServer);

        this.domainClass = method.getEntityInformation().getJavaType();
        this.tree = new PartTree(method.getName(), domainClass);
        this.parameters = (DefaultParameters) method.getParameters();
        this.queryPreparer = new QueryPreparer(ebeanServer);
    }

    @Override
    protected AbstractEbeanQueryExecution getExecution() {
        if (this.tree.isDelete()) {
            return new AbstractEbeanQueryExecution.DeleteExecution(getEbeanServer());
        } else if (this.tree.isExistsProjection()) {
            return new AbstractEbeanQueryExecution.ExistsExecution();
        }

        return super.getExecution();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.ebean.repository.query.AbstractEbeanQuery#doCreateQuery(java.lang.Object[])
     */
    @Override
    public EbeanQueryWrapper doCreateQuery(Object[] values) {
        return queryPreparer.createQuery(values);
    }

    /**
     * EbeanQueryWrapper preparer to create {@link Query} instances and potentially cache them.
     *
     * @author Xuegui Yuan
     */
    private class QueryPreparer {

        private final EbeanServer ebeanServer;

        public QueryPreparer(EbeanServer ebeanServer) {
            this.ebeanServer = ebeanServer;
        }

        /**
         * Creates a new {@link Query} for the given parameter values.
         *
         * @param values
         * @return
         */
        public EbeanQueryWrapper createQuery(Object[] values) {
            ParametersParameterAccessor accessor = new ParametersParameterAccessor(parameters, values);
            EbeanQueryCreator ebeanQueryCreator = createCreator(accessor);
            return
                    restrictMaxResultsIfNecessary(
                            invokeBinding(getBinder(values),
                                    EbeanQueryWrapper.ofEbeanQuery(ebeanQueryCreator.createQuery())));
        }

        protected EbeanQueryCreator createCreator(ParametersParameterAccessor accessor) {
            EbeanServer ebeanServer = getEbeanServer();
            Query ebeanQuery = ebeanServer.createQuery(domainClass);
            ExpressionList expressionList = ebeanQuery.where();

            ParameterMetadataProvider provider = new ParameterMetadataProvider(accessor);

            ResultProcessor processor = getQueryMethod().getResultProcessor();

            return new EbeanQueryCreator(tree, processor.getReturnedType(), expressionList, provider);
        }

        /**
         * Restricts the max results of the given {@link Query} if the current {@code tree} marks this {@code query} as
         * limited.
         *
         * @param query
         * @return
         */
        private EbeanQueryWrapper restrictMaxResultsIfNecessary(EbeanQueryWrapper query) {
            if (tree.isLimiting()) {

                if (query.getMaxRows() != Integer.MAX_VALUE) {
                    /*
                     * In order to return the correct results, we have to adjust the first result offset to be returned if:
					 * - a Pageable parameter is present
					 * - AND the requested page number > 0
					 * - AND the requested page size was bigger than the derived result limitation via the First/Top keyword.
					 */
                    if (query.getMaxRows() > tree.getMaxResults() && query.getFirstRow() > 0) {
                        query.setFirstRow(query.getFirstRow() - (query.getMaxRows() - tree.getMaxResults()));
                    }
                }

                query.setMaxRows(tree.getMaxResults());
            }

            if (tree.isExistsProjection()) {
                query.setMaxRows(1);
            }

            return query;
        }

        /**
         * Invokes parameter binding on the given {@link ExpressionList}.
         *
         * @param binder
         * @param query
         * @return
         */
        protected EbeanQueryWrapper invokeBinding(ParameterBinder binder, EbeanQueryWrapper query) {
            return binder.bindAndPrepare(query);
        }

        private ParameterBinder getBinder(Object[] values) {
            return new ParameterBinder(parameters, values);
        }

        private Sort getDynamicSort(Object[] values) {
            return parameters.potentiallySortsDynamically() ? new ParametersParameterAccessor(parameters, values).getSort()
                    : null;
        }
    }
}
