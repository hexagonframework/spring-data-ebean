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
import org.springframework.data.repository.query.*;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;


/**
 * Base class for {@link String} based Ebean queries.
 *
 * @author Xuegui Yuan
 */
abstract class AbstractStringBasedEbeanQuery extends AbstractEbeanQuery {

    private final StringQuery query;
    private final EvaluationContextProvider evaluationContextProvider;
    private final SpelExpressionParser parser;

    /**
     * Creates a new {@link AbstractStringBasedEbeanQuery} from the given {@link EbeanQueryMethod}, {@link io.ebean.EbeanServer} and
     * query {@link String}.
     *
     * @param method                    must not be {@literal null}.
     * @param ebeanServer               must not be {@literal null}.
     * @param queryString               must not be {@literal null}.
     * @param evaluationContextProvider must not be {@literal null}.
     * @param parser                    must not be {@literal null}.
     */
    public AbstractStringBasedEbeanQuery(EbeanQueryMethod method, EbeanServer ebeanServer, String queryString,
                                         EvaluationContextProvider evaluationContextProvider, SpelExpressionParser parser) {

        super(method, ebeanServer);

        Assert.hasText(queryString, "EbeanQueryWrapper string must not be null or empty!");
        Assert.notNull(evaluationContextProvider, "ExpressionEvaluationContextProvider must not be null!");
        Assert.notNull(parser, "Parser must not be null or empty!");

        this.evaluationContextProvider = evaluationContextProvider;
        this.query = new ExpressionBasedStringQuery(queryString, method.getEntityInformation(), parser);
        this.parser = parser;
    }

    /**
     * @return the query
     */
    public StringQuery getQuery() {
        return query;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.ebean.repository.query.AbstractEbeanQuery#doCreateQuery(java.lang.Object[])
     */
    @Override
    public EbeanQueryWrapper doCreateQuery(Object[] values) {
        ParameterAccessor accessor = new ParametersParameterAccessor(getQueryMethod().getParameters(), values);

        EbeanQueryWrapper query = createEbeanQuery(this.query.getQueryString());

        return createBinder(values).bindAndPrepare(query);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.ebean.repository.query.AbstractEbeanQuery#createBinder(java.lang.Object[])
     */
    @Override
    protected ParameterBinder createBinder(Object[] values) {
        return new SpelExpressionStringQueryParameterBinder((DefaultParameters) getQueryMethod().getParameters(), values, query,
                evaluationContextProvider, parser);
    }

    /**
     * Creates an appropriate Ebean query from an {@link EbeanServer} according to the current {@link AbstractEbeanQuery}
     * type.
     *
     * @param queryString
     * @return
     */
    protected EbeanQueryWrapper createEbeanQuery(String queryString) {
        EbeanServer ebeanServer = getEbeanServer();

        ResultProcessor resultFactory = getQueryMethod().getResultProcessor();
        ReturnedType returnedType = resultFactory.getReturnedType();

        return EbeanQueryWrapper.ofEbeanQuery(ebeanServer.createQuery(returnedType.getReturnedType(), queryString));
    }
}
