/*
 * Copyright 2013-2016 the original author or authors.
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
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.ResultProcessor;
import org.springframework.data.repository.query.ReturnedType;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * {@link RepositoryQuery} implementation that inspects a {@link org.springframework.data.repository.query.QueryMethod}
 * for the existence of an {@link Query} annotation and creates a Ebean native
 * {@link io.ebean.Query} from it.
 *
 * @author Xuegui Yuan
 */
final class NativeEbeanQuery extends AbstractStringBasedEbeanQuery {

    /**
     * Creates a new {@link NativeEbeanQuery} encapsulating the query annotated on the given {@link EbeanQueryMethod}.
     *
     * @param method                    must not be {@literal null}.
     * @param ebeanServer               must not be {@literal null}.
     * @param queryString               must not be {@literal null} or empty.
     * @param evaluationContextProvider
     */
    public NativeEbeanQuery(EbeanQueryMethod method, EbeanServer ebeanServer, String queryString,
                            EvaluationContextProvider evaluationContextProvider, SpelExpressionParser parser) {

        super(method, ebeanServer, queryString, evaluationContextProvider, parser);

        Parameters<?, ?> parameters = method.getParameters();
        boolean hasPagingOrSortingParameter = parameters.hasPageableParameter() || parameters.hasSortParameter();
        boolean containsPageableOrSortInQueryExpression = queryString.contains("#pageable")
                || queryString.contains("#sort");

        if (hasPagingOrSortingParameter && !containsPageableOrSortInQueryExpression) {
            throw new InvalidEbeanQueryMethodException(
                    "Cannot use native queries with dynamic sorting and/or pagination in method " + method);
        }
    }

    protected Object createEbeanQuery(String queryString) {
        ResultProcessor resultFactory = getQueryMethod().getResultProcessor();
        ReturnedType returnedType = resultFactory.getReturnedType();

        return getEbeanServer().findNative(returnedType.getReturnedType(), queryString);
    }
}
