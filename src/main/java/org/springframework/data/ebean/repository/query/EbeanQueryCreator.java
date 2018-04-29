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

import io.ebean.Expr;
import io.ebean.Expression;
import io.ebean.ExpressionList;
import io.ebean.Query;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.ReturnedType;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * EbeanQueryWrapper creator to create a {@link io.ebean.Expression} from a {@link PartTree}.
 *
 * @author Xuegui Yuan
 */
public class EbeanQueryCreator extends AbstractQueryCreator<Query, Expression> {

    private final ExpressionList root;
    private final ParameterMetadataProvider provider;
    private final ReturnedType returnedType;
    private final PartTree tree;

    /**
     * Create a new {@link EbeanQueryCreator}.
     *
     * @param tree           must not be {@literal null}.
     * @param type           must not be {@literal null}.
     * @param expressionList must not be {@literal null}.
     * @param provider       must not be {@literal null}.
     */
    public EbeanQueryCreator(PartTree tree, ReturnedType type, ExpressionList expressionList,
                             ParameterMetadataProvider provider) {
        super(tree);
        this.tree = tree;

        this.root = expressionList;
        this.provider = provider;
        this.returnedType = type;
    }

    /**
     * Returns all {@link ParameterMetadataProvider.ParameterMetadata} created when creating the query.
     *
     * @return the parameterExpressions
     */
    public List<ParameterMetadataProvider.ParameterMetadata<?>> getParameterExpressions() {
        return provider.getExpressions();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.query.parser.AbstractQueryCreator#create(org.springframework.data.repository.query.parser.Part, java.util.Iterator)
     */
    @Override
    protected Expression create(Part part, Iterator<Object> iterator) {
        return toExpression(part, root);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.query.parser.AbstractQueryCreator#and(org.springframework.data.repository.query.parser.Part, java.lang.Object, java.util.Iterator)
     */
    @Override
    protected Expression and(Part part, Expression base, Iterator<Object> iterator) {
        return Expr.and(base, toExpression(part, root));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.query.parser.AbstractQueryCreator#or(java.lang.Object, java.lang.Object)
     */
    @Override
    protected Expression or(Expression base, Expression expression) {
        return Expr.or(base, expression);
    }

    /**
     * Finalizes the given {@link ExpressionList} and applies the given sort.
     */
    @Override
    protected final Query complete(Expression expression, Sort sort) {
        return root.add(expression).query();
    }

    /**
     * Creates a {@link ExpressionList} from the given {@link Part}.
     *
     * @param part
     * @param root
     * @return
     */
    private Expression toExpression(Part part, ExpressionList<?> root) {
        return new ExpressionBuilder(part, root).build();
    }

    /**
     * Simple builder to contain logic to create Ebean {@link Expression}s from {@link Part}s.
     *
     * @author Xuegui Yuan
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private class ExpressionBuilder {

        private final Part part;
        private final ExpressionList root;

        /**
         * Creates a new {@link ExpressionBuilder} for the given {@link Part} and {@link ExpressionList}.
         *
         * @param part must not be {@literal null}.
         * @param root must not be {@literal null}.
         */
        public ExpressionBuilder(Part part, ExpressionList<?> root) {
            Assert.notNull(part, "Part must not be null!");
            Assert.notNull(root, "ExpressionList must not be null!");
            this.part = part;
            this.root = root;
        }

        /**
         * Builds a Ebean {@link Expression} from the underlying {@link Part}.
         *
         * @return
         */
        public Expression build() {
            PropertyPath property = part.getProperty();
            Part.Type type = part.getType();

            switch (type) {
                case BETWEEN:
                    ParameterMetadataProvider.ParameterMetadata<Comparable> first = provider.next(part);
                    ParameterMetadataProvider.ParameterMetadata<Comparable> second = provider.next(part);
                    return Expr.between(property.toDotPath(), first.getParameterValue(), second.getParameterValue());
                case AFTER:
                case GREATER_THAN:
                    return Expr.gt(property.toDotPath(), provider.next(part).getParameterValue());
                case GREATER_THAN_EQUAL:
                    return Expr.ge(property.toDotPath(), provider.next(part).getParameterValue());
                case BEFORE:
                case LESS_THAN:
                    return Expr.lt(property.toDotPath(), provider.next(part).getParameterValue());
                case LESS_THAN_EQUAL:
                    return Expr.le(property.toDotPath(), provider.next(part).getParameterValue());
                case IS_NULL:
                    return Expr.isNull(property.toDotPath());
                case IS_NOT_NULL:
                    return Expr.isNotNull(property.toDotPath());
                case NOT_IN:
                    ParameterMetadataProvider.ParameterMetadata<? extends Collection> pmNotIn = provider.next(part, Collection.class);
                    return Expr.not(Expr.in(property.toDotPath(), ParameterMetadataProvider.ParameterMetadata.toCollection(pmNotIn.getParameterValue())));
                case IN:
                    ParameterMetadataProvider.ParameterMetadata<? extends Collection> pmIn = provider.next(part, Collection.class);
                    return Expr.in(property.toDotPath(), ParameterMetadataProvider.ParameterMetadata.toCollection(pmIn.getParameterValue()));
                case STARTING_WITH:
                    return Expr.startsWith(property.toDotPath(), (String) provider.next(part).getParameterValue());
                case ENDING_WITH:
                    return Expr.endsWith(property.toDotPath(), (String) provider.next(part).getParameterValue());
                case CONTAINING:
                    return Expr.contains(property.toDotPath(), (String) provider.next(part).getParameterValue());
                case NOT_CONTAINING:
                    return Expr.not(Expr.contains(property.toDotPath(), (String) provider.next(part).getParameterValue()));
                case LIKE:
                    return Expr.like(property.toDotPath(), (String) provider.next(part).getParameterValue());
                case NOT_LIKE:
                    return Expr.not(Expr.like(property.toDotPath(), (String) provider.next(part).getParameterValue()));
                case TRUE:
                    return Expr.eq(property.toDotPath(), true);
                case FALSE:
                    return Expr.eq(property.toDotPath(), false);
                case SIMPLE_PROPERTY:
                    ParameterMetadataProvider.ParameterMetadata<Object> pmEquals = provider.next(part);
                    return pmEquals.isIsNullParameter() ? Expr.isNull(property.toDotPath())
                            : Expr.eq(property.toDotPath(), pmEquals.getParameterValue());
                case NEGATING_SIMPLE_PROPERTY:
                    ParameterMetadataProvider.ParameterMetadata<Object> pmNot = provider.next(part);
                    return pmNot.isIsNullParameter() ? Expr.isNull(property.toDotPath())
                            : Expr.ne(property.toDotPath(), pmNot.getParameterValue());
//        case IS_EMPTY:
//          return Expr.isEmpty(property.toDotPath());
//        case IS_NOT_EMPTY:
//          return Expr.isNotEmpty(property.toDotPath());
                default:
                    throw new IllegalArgumentException("Unsupported keyword " + type);
            }
        }
    }
}
