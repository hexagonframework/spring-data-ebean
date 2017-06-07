/*
 * Copyright 2008-2017 the original author or authors.
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

import io.ebean.ExpressionList;
import org.springframework.data.repository.query.ReturnedType;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.PartTree;

/**
 * EbeanQuery creator to create a {@link io.ebean.ExpressionList} from a {@link PartTree}.
 *
 * @author Oliver Gierke
 * @author Mark Paluch
 * @author Michael Cramer
 */
public abstract class EbeanQueryCreator extends AbstractQueryCreator<ExpressionList, ExpressionList> {

    private final ExpressionList expressionList;
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

        this.expressionList = expressionList;
        this.provider = provider;
        this.returnedType = type;
    }
//
//	/**
//	 * Returns all {@link ParameterMetadata} created when creating the query.
//	 *
//	 * @return the parameterExpressions
//	 */
//	public List<ParameterMetadata<?>> getParameterExpressions() {
//		return provider.getExpressions();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.repository.query.parser.AbstractQueryCreator#create(org.springframework.data.repository.query.parser.Part, java.util.Iterator)
//	 */
//	@Override
//	protected ExpressionList create(Part part, Iterator<Object> iterator) {
//		return toPredicate(part, expressionList);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.repository.query.parser.AbstractQueryCreator#and(org.springframework.data.repository.query.parser.Part, java.lang.Object, java.util.Iterator)
//	 */
//	@Override
//	protected ExpressionList and(Part part, Expression base, Iterator<Object> iterator) {
//		return expressionList.and(base, toPredicate(part, expressionList));
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.repository.query.parser.AbstractQueryCreator#or(java.lang.Object, java.lang.Object)
//	 */
//	@Override
//	protected ExpressionList or(Expression base, Expression predicate) {
//		return expressionList.or(base, predicate);
//	}
//
//	/**
//	 * Finalizes the given {@link ExpressionList} and applies the given sort.
//	 */
//	@Override
//	protected final ExpressionList complete(ExpressionList predicate, Sort sort) {
//		return predicate;
//	}
//	/**
//	 * Creates a {@link ExpressionList} from the given {@link Part}.
//	 *
//	 * @param part
//	 * @param root
//	 * @return
//	 */
//	private ExpressionList toPredicate(Part part, ExpressionList<?> root) {
//		return new PredicateBuilder(part, root).build();
//	}
//
//	/**
//	 * Simple builder to contain logic to create Ebean {@link ExpressionList}s from {@link Part}s.
//	 *
//	 * @author Xuegui Yuan
//	 */
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	private class PredicateBuilder {
//
//		private final Part part;
//		private final ExpressionList root;
//
//		/**
//		 * Creates a new {@link PredicateBuilder} for the given {@link Part} and {@link Root}.
//		 *
//		 * @param part must not be {@literal null}.
//		 * @param root must not be {@literal null}.
//		 */
//		public PredicateBuilder(Part part, ExpressionList<?> root) {
//
//			Assert.notNull(part, "Part must not be null!");
//			Assert.notNull(root, "ExpressionList must not be null!");
//			this.part = part;
//			this.root = root;
//		}
//
//		/**
//		 * Builds a Ebean {@link ExpressionList} from the underlying {@link Part}.
//		 *
//		 * @return
//		 */
//		public ExpressionList build() {
//
//			PropertyPath property = part.getProperty();
//			Type type = part.getType();
//
//			switch (type) {
//				case BETWEEN:
//					ParameterMetadata<Comparable> first = provider.next(part);
//					ParameterMetadata<Comparable> second = provider.next(part);
//					return expressionList.between(part.getProperty().getSegment(), first.getExpression(), second.getExpression());
//				case AFTER:
//				case GREATER_THAN:
//					return expressionList.gt(part.getProperty().getSegment(),
//							provider.next(part, Comparable.class).getExpression());
//				case GREATER_THAN_EQUAL:
//					return expressionList.ge(part.getProperty().getSegment(),
//							provider.next(part, Comparable.class).getExpression());
//				case BEFORE:
//				case LESS_THAN:
//					return expressionList.lt(part.getProperty().getSegment(),
//							provider.next(part, Comparable.class).getExpression());
//				case LESS_THAN_EQUAL:
//					return expressionList.le(part.getProperty().getSegment(),
//							provider.next(part, Comparable.class).getExpression());
//				case IS_NULL:
//					return expressionList.isNull(part.getProperty().getSegment());
//				case IS_NOT_NULL:
//					return expressionList.isNotNull(part.getProperty().getSegment());
//				case NOT_IN:
//					return expressionList.notIn(part.getProperty().getSegment(), provider.next(part, Collection.class).getExpression());
//				case IN:
//					return expressionList.in(part.getProperty().getSegment(), provider.next(part, Collection.class).getExpression());
//				case STARTING_WITH:
//					return expressionList.startsWith(part.getProperty().getSegment(),
//							String.valueOf(provider.next(part).getExpression()));
//				case ENDING_WITH:
//					return expressionList.endsWith(part.getProperty().getSegment(),
//							String.valueOf(provider.next(part).getExpression()));
//				case CONTAINING:
//					return expressionList.contains(part.getProperty().getSegment(),
//							String.valueOf(provider.next(part).getExpression()));
//				//case NOT_CONTAINING:
//				case LIKE:
//					return expressionList.like(part.getProperty().getSegment(),
//							String.valueOf(provider.next(part).getExpression()));
//				//case NOT_LIKE:
//				case TRUE:
//					return expressionList.eq(part.getProperty().getSegment(), true);
//				case FALSE:
//					return expressionList.eq(part.getProperty().getSegment(), false);
//				//case SIMPLE_PROPERTY:
//				//case NEGATING_SIMPLE_PROPERTY:
////				case IS_EMPTY:
////					return expressionList.isEmpty(part.getProperty().getSegment());
////				case IS_NOT_EMPTY:
////					return expressionList.isEmpty(part.getProperty().getSegment());
//				default:
//					throw new IllegalArgumentException("Unsupported keyword " + type);
//			}
//		}
//	}
}
