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

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.ebean.annotation.Modifying;
import org.springframework.data.ebean.annotation.Query;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Ebean specific extension of {@link QueryMethod}.
 *
 * @author Xuegui Yuan
 */
public class EbeanQueryMethod extends QueryMethod {

    private final Method method;

    /**
     * Creates a {@link EbeanQueryMethod}.
     *
     * @param method   must not be {@literal null}
     * @param metadata must not be {@literal null}
     */
    public EbeanQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
        super(method, metadata, factory);

        Assert.notNull(method, "Method must not be null!");

        this.method = method;
    }

    /**
     * Returns the actual return type of the method.
     *
     * @return
     */
    Class<?> getReturnType() {
        return method.getReturnType();
    }

    /**
     * Returns the query string declared in a {@link Query} annotation or {@literal null} if neither the annotation found
     * nor the attribute was specified.
     *
     * @return
     */
    String getAnnotatedQuery() {
        String query = getAnnotationValue("value", String.class);
        return StringUtils.hasText(query) ? query : null;
    }

    /**
     * Returns the {@link Query} annotation's attribute casted to the given type or default value if no annotation
     * available.
     *
     * @param attribute
     * @param type
     * @return
     */
    private <T> T getAnnotationValue(String attribute, Class<T> type) {
        return getMergedOrDefaultAnnotationValue(attribute, Query.class, type);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T> T getMergedOrDefaultAnnotationValue(String attribute, Class annotationType, Class<T> targetType) {
        Annotation annotation = AnnotatedElementUtils.findMergedAnnotation(method, annotationType);
        if (annotation == null) {
            return targetType.cast(AnnotationUtils.getDefaultValue(annotationType, attribute));
        }

        return targetType.cast(AnnotationUtils.getValue(annotation, attribute));
    }

    /**
     * Returns whether the backing query is a native one.
     *
     * @return
     */
    boolean isNativeQuery() {
        return getAnnotationValue("nativeQuery", Boolean.class).booleanValue();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.query.QueryMethod#getNamedQueryName()
     */
    @Override
    public String getNamedQueryName() {
        String annotatedName = getAnnotationValue("name", String.class);
        return StringUtils.hasText(annotatedName) ? annotatedName : super.getNamedQueryName();
    }

    /**
     * Returns whether the finder is a modifying one.
     *
     * @return
     */
    @Override
    public boolean isModifyingQuery() {
        return null != AnnotationUtils.findAnnotation(method, Modifying.class);
    }
}
