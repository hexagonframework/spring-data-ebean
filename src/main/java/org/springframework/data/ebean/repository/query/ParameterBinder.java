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

import io.ebean.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.*;
import org.springframework.util.Assert;

/**
 * {@link ParameterBinder} is used to bind method parameters to a {@link Query}. This is usually done whenever an
 * {@link AbstractEbeanQuery} is executed.
 *
 * @author Xuegui Yuan
 */
public class ParameterBinder {

    private final DefaultParameters parameters;
    private final ParameterAccessor accessor;
    private final Object[] values;

    ParameterBinder(DefaultParameters parameters) {
        this(parameters, new Object[0]);
    }

    /**
     * Creates a new {@link ParameterBinder}.
     *
     * @param parameters must not be {@literal null}.
     * @param values     must not be {@literal null}.
     */
    public ParameterBinder(DefaultParameters parameters, Object[] values) {

        Assert.notNull(parameters, "Parameters must not be null!");
        Assert.notNull(values, "Values must not be null!");

        Assert.isTrue(parameters.getNumberOfParameters() == values.length, "Invalid number of parameters given!");

        this.parameters = parameters;
        this.values = values.clone();
        this.accessor = new ParametersParameterAccessor(parameters, this.values);
    }

    /**
     * Returns the sort instance to be used for query creation. Will use a {@link Sort} parameter if available or the
     * {@link Sort} contained in a {@link Pageable} if available. Returns {@code null} if no {@link Sort} can be found.
     *
     * @return
     */
    public Sort getSort() {
        return accessor.getSort();
    }

    /**
     * Binds the parameters to the given query and applies special parameter types (e.g. pagination).
     *
     * @param query must not be {@literal null}.
     * @return
     */
    public EbeanQueryWrapper bindAndPrepare(EbeanQueryWrapper query) {
        Assert.notNull(query, "query must not be null!");
        return bindAndPrepare(query, parameters);
    }

    private EbeanQueryWrapper bindAndPrepare(EbeanQueryWrapper query, Parameters<?, ?> parameters) {
        EbeanQueryWrapper result = bind(query);

        if (!parameters.hasPageableParameter()) {
            return result;
        }

        result.setFirstRow((int) getPageable().getOffset());
        result.setMaxRows(getPageable().getPageSize());

        return result;
    }

    /**
     * Binds the parameters to the given {@link Query}.
     *
     * @param query must not be {@literal null}.
     * @return
     */
    public EbeanQueryWrapper bind(EbeanQueryWrapper query) {

        Assert.notNull(query, "EbeanQueryWrapper must not be null!");

        int bindableParameterIndex = 0;
        int queryParameterPosition = 1;

        for (Parameter parameter : parameters) {

            if (canBindParameter(parameter)) {

                Object value = accessor.getBindableValue(bindableParameterIndex);
                bind(query, parameter, value, queryParameterPosition++);
                bindableParameterIndex++;
            }
        }

        return query;
    }

    /**
     * Returns the {@link Pageable} of the parameters, if available. Returns {@code null} otherwise.
     *
     * @return
     */
    public Pageable getPageable() {
        return accessor.getPageable();
    }

    /**
     * Returns {@literal true} if the given parameter can be bound.
     *
     * @param parameter
     * @return
     */
    protected boolean canBindParameter(Parameter parameter) {
        return parameter.isBindable();
    }

    /**
     * Perform the actual query parameter binding.
     *
     * @param query
     * @param parameter
     * @param value
     * @param position
     */
    protected void bind(EbeanQueryWrapper query, Parameter parameter, Object value, int position) {
        if (parameter.isNamedParameter()) {
            query.setParameter(parameter.getName().get(), value);
        } else {
            query.setParameter(position, value);
        }
    }

    /**
     * Returns the parameters.
     *
     * @return
     */
    Parameters getParameters() {
        return parameters;
    }

    protected Object[] getValues() {
        return values;
    }
}
