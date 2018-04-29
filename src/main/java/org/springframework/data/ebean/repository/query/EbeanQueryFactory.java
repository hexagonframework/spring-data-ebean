/*
 * Copyright 2013 the original author or authors.
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
import io.ebean.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.expression.spel.standard.SpelExpressionParser;


/**
 * Factory to create the appropriate {@link RepositoryQuery} for a {@link EbeanQueryMethod}.
 *
 * @author Xuegui Yuan
 */
enum EbeanQueryFactory {

    /**
     * Instance
     */
    INSTANCE;

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    private static final Logger LOG = LoggerFactory.getLogger(EbeanQueryFactory.class);

    /**
     * Creates a {@link RepositoryQuery} from the given {@link QueryMethod} that is potentially annotated with
     * {@link Query}.
     *
     * @param method                    must not be {@literal null}.
     * @param ebeanServer               must not be {@literal null}.
     * @param evaluationContextProvider
     * @return the {@link RepositoryQuery} derived from the annotation or {@code null} if no annotation found.
     */
    AbstractEbeanQuery fromQueryAnnotation(EbeanQueryMethod method, EbeanServer ebeanServer,
                                           EvaluationContextProvider evaluationContextProvider) {

        LOG.debug("Looking up query for method {}", method.getName());
        return fromMethodWithQueryString(method, ebeanServer, method.getAnnotatedQuery(), evaluationContextProvider);
    }

    /**
     * Creates a {@link RepositoryQuery} from the given {@link String} query.
     *
     * @param method                    must not be {@literal null}.
     * @param ebeanServer               must not be {@literal null}.
     * @param queryString               must not be {@literal null} or empty.
     * @param evaluationContextProvider
     * @return
     */
    AbstractEbeanQuery fromMethodWithQueryString(EbeanQueryMethod method, EbeanServer ebeanServer, String queryString,
                                                 EvaluationContextProvider evaluationContextProvider) {

        if (queryString == null) {
            return null;
        }
        // native
        if (method.isNativeQuery()) {
            if (method.isModifyingQuery()) {
                return new NativeEbeanUpdate(method, ebeanServer, queryString, evaluationContextProvider, PARSER);
            } else {
                return new NativeEbeanQuery(method, ebeanServer, queryString, evaluationContextProvider, PARSER);
            }
        } else { // ORM
            if (method.isModifyingQuery()) {
                return new OrmEbeanUpdate(method, ebeanServer, queryString, evaluationContextProvider, PARSER);
            } else {
                return new OrmEbeanQuery(method, ebeanServer, queryString, evaluationContextProvider, PARSER);
            }
        }
    }

}
