/*
 * Copyright 2011-2017 the original author or authors.
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

import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.Part.Type;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * Helper class to allow easy creation of {@link ParameterMetadata}s.
 *
 * @author Xuegui Yuan
 */
class ParameterMetadataProvider {
    private final Iterator<? extends Parameter> parameters;
    private final List<ParameterMetadata<?>> expressions;
    private final Iterator<Object> bindableParameterValues;

    /**
     * Creates a new {@link ParameterMetadataProvider} from the given
     * {@link ParametersParameterAccessor} with impl for parameter value customizations.
     *
     * @param accessor must not be {@literal null}.
     */
    public ParameterMetadataProvider(ParametersParameterAccessor accessor) {
        this(accessor.iterator(), accessor.getParameters());
    }

    /**
     * Creates a new {@link ParameterMetadataProvider} from the given {@link Iterable} of all
     * bindable parameter values.
     *
     * @param bindableParameterValues may be {@literal null}.
     * @param parameters              must not be {@literal null}.
     */
    private ParameterMetadataProvider(Iterator<Object> bindableParameterValues,
                                      Parameters<?, ?> parameters) {
        Assert.notNull(parameters, "Parameters must not be null!");

        this.parameters = parameters.getBindableParameters().iterator();
        this.expressions = new ArrayList<ParameterMetadata<?>>();
        this.bindableParameterValues = bindableParameterValues;
    }

    /**
     * Returns all {@link ParameterMetadata}s built.
     *
     * @return the expressions
     */
    public List<ParameterMetadata<?>> getExpressions() {
        return Collections.unmodifiableList(expressions);
    }

    /**
     * Builds a new {@link ParameterMetadata} for given {@link Part} and the next {@link Parameter}.
     *
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> ParameterMetadata<T> next(Part part) {
        Parameter parameter = parameters.next();
        return (ParameterMetadata<T>) next(part, parameter.getType(), parameter);
    }

    /**
     * Builds a new {@link ParameterMetadata} for the given type and name.
     *
     * @param <T>
     * @param part      must not be {@literal null}.
     * @param type      parameter type, must not be {@literal null}.
     * @param parameter
     * @return
     */
    private <T> ParameterMetadata<T> next(Part part, Class<T> type, Parameter parameter) {
        Assert.notNull(type, "Type must not be null!");

        ParameterMetadata<T> value = new ParameterMetadata<T>(type, parameter.getName().get(), part.getType(),
                bindableParameterValues == null ? ParameterMetadata.PLACEHOLDER : bindableParameterValues.next());
        expressions.add(value);

        return value;
    }

    /**
     * Builds a new {@link ParameterMetadata} of the given {@link Part} and type. Forwards the underlying
     * {@link Parameters} as well.
     *
     * @param <T>
     * @param type must not be {@literal null}.
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> ParameterMetadata<? extends T> next(Part part, Class<T> type) {
        Parameter parameter = parameters.next();
        Class<?> typeToUse = ClassUtils.isAssignable(type, parameter.getType()) ? parameter.getType() : type;
        return (ParameterMetadata<? extends T>) next(part, typeToUse, parameter);
    }

    /**
     * @param <T>
     * @author Xuegui Yuan
     */
    static class ParameterMetadata<T> {

        static final Object PLACEHOLDER = new Object();

        private final Type type;
        private final Class<T> parameterType;
        private final String parameterName;
        private final Object parameterValue;

        /**
         * Creates a new {@link ParameterMetadata}.
         *
         * @param parameterType
         * @param parameterName
         * @param type
         * @param value
         */
        public ParameterMetadata(Class<T> parameterType, String parameterName, Type type, Object value) {
            this.parameterType = parameterType;
            this.parameterName = parameterName;
            this.parameterValue = value;
            this.type = (value == null && Type.SIMPLE_PROPERTY.equals(type) ? Type.IS_NULL : type);
        }

        /**
         * Returns the given argument as {@link Collection} which means it will return it as is if it's a
         * {@link Collections}, turn an array into an {@link ArrayList} or simply wrap any other value into a single element
         * {@link Collections}.
         *
         * @param value
         * @return
         */
        public static Collection<?> toCollection(Object value) {
            if (value == null) {
                return null;
            }

            if (value instanceof Collection) {
                return (Collection<?>) value;
            }

            if (ObjectUtils.isArray(value)) {
                return Arrays.asList(ObjectUtils.toObjectArray(value));
            }

            return Collections.singleton(value);
        }

        /**
         * Returns whether the parameter shall be considered an {@literal IS NULL} parameter.
         *
         * @return
         */
        public boolean isIsNullParameter() {
            return Type.IS_NULL.equals(type);
        }

        public Class<T> getParameterType() {
            return parameterType;
        }

        public String getParameterName() {
            return parameterName;
        }

        public Object getParameterValue() {
            return parameterValue;
        }
    }
}
