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

package org.springframework.data.ebean.querychannel;

import io.ebean.*;
import io.ebean.text.PathProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.data.ebean.annotation.ExprParam;
import org.springframework.data.ebean.annotation.IncludeFields;
import org.springframework.data.ebean.util.Converters;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Query channel service class.
 *
 * @author Xuegui Yuan
 */
public class EbeanQueryChannelService implements QueryChannelService {

    private EbeanServer ebeanServer;

    public EbeanQueryChannelService() {
        this.ebeanServer = Ebean.getDefaultServer();
    }

    public EbeanQueryChannelService(String serverName) {
        this.ebeanServer = Ebean.getServer(serverName);
    }

    public EbeanQueryChannelService(EbeanServer ebeanServer) {
        this.ebeanServer = ebeanServer;
    }

    /**
     * Return the current EbeanServer.
     *
     * @return the current EbeanServer
     */
    public static EbeanServer db() {
        return Ebean.getDefaultServer();
    }

    /**
     * Return the EbeanServer by name.
     *
     * @return the named EbeanServer
     */
    public static EbeanServer db(String name) {
        return Ebean.getServer(name);
    }

    /**
     * Return an object relational query for finding a List, Set, Map or single entity bean.
     *
     * @return the Query.
     */
    public static <T> Query<T> query(Class<T> entityType) {
        Assert.notNull(entityType, "entityType must not null");
        return Ebean.find(entityType);
    }

    /**
     * Return an query for finding a List, Set, Map or single bean.
     *
     * @return the Query.
     */
    public static <T> Query<T> query(Class<T> entityType, Object queryObject) {
        return query(db(), entityType, null, queryObject, null);
    }

    /**
     * Return an query for finding a List, Set, Map or single bean.
     *
     * @return the Query.
     */
    public static <T> Query<T> query(Class<T> entityType, Object queryObject, Pageable pageable) {
        return query(db(), entityType, null, queryObject, pageable);
    }

    /**
     * Return an query for finding a List, Set, Map or single bean.
     *
     * @return the Query.
     */
    public static <T> Query<T> query(Class<T> entityType, String fetchPath, Object queryObject) {
        return query(db(), entityType, fetchPath, queryObject, null);
    }

    /**
     * Return an query for finding a List, Set, Map or single bean.
     *
     * @return the Query.
     */
    public static <T> Query<T> query(Class<T> entityType, String fetchPath, Object queryObject, Pageable pageable) {
        return query(db(), entityType, fetchPath, queryObject, pageable);
    }

    /**
     * Add expr defined in queryObject to expressionList.
     *
     * @param expressionList the expressionList to add expr.
     * @param queryObject    expr defined in.
     * @param <T>            bean type.
     * @return the expressionList.
     */
    public static <T> ExpressionList<T> applyWhere(ExpressionList<T> expressionList, Object queryObject) {
        if (queryObject != null) {
            Class clz = queryObject.getClass();
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String param = field.getName();
                Object value = null;
                try {
                    value = field.get(queryObject);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                ExprType exprType = ExprType.DEFAULT;
                boolean ignoreCase = false;
                boolean escapeNull = true;
                boolean isString = field.getType() == String.class;
                boolean isArray = field.getType().isArray();
                if (field.isAnnotationPresent(ExprParam.class)) {
                    ExprParam exprParam = field.getAnnotation(ExprParam.class);
                    String qpv = exprParam.value();
                    String qpn = exprParam.name();
                    exprType = exprParam.expr();
                    ignoreCase = exprParam.ignoreCase();
                    escapeNull = exprParam.escapeNull();
                    if (escapeNull && value == null) {
                        break;
                    }
                    if (StringUtils.hasText(qpv)) {
                        param = qpv;
                    }
                    if (StringUtils.hasText(qpn)) {
                        param = qpn;
                    }
                }
                switch (exprType) {
                    case EQ:
                        if (isString && ignoreCase) {
                            expressionList.add(Expr.ieq(param, (String) value));
                        } else {
                            expressionList.add(Expr.eq(param, value));
                        }
                        break;
                    case NE:
                        expressionList.add(Expr.ne(param, value));
                        break;
                    case GE:
                        expressionList.add(Expr.ge(param, value));
                        break;
                    case GT:
                        expressionList.add(Expr.gt(param, value));
                        break;
                    case LE:
                        expressionList.add(Expr.le(param, value));
                        break;
                    case LT:
                        expressionList.add(Expr.lt(param, value));
                        break;
                    case LIKE:
                        if (isString) {
                            if (ignoreCase) {
                                expressionList.add(Expr.ilike(param, (String) value));
                            } else {
                                expressionList.add(Expr.like(param, (String) value));
                            }
                        }
                        break;
                    case STARTS_WITH:
                        if (isString) {
                            if (ignoreCase) {
                                expressionList.add(Expr.istartsWith(param, (String) value));
                            } else {
                                expressionList.add(Expr.startsWith(param, (String) value));
                            }
                        }
                        break;
                    case ENDS_WITH:
                        if (isString) {
                            if (ignoreCase) {
                                expressionList.add(Expr.iendsWith(param, (String) value));
                            } else {
                                expressionList.add(Expr.endsWith(param, (String) value));
                            }
                        }
                        break;
                    case CONTAINS:
                        if (isString) {
                            if (ignoreCase) {
                                expressionList.add(Expr.icontains(param, (String) value));
                            } else {
                                expressionList.add(Expr.contains(param, (String) value));
                            }
                        }
                        break;
                    case IN:
                        if (isArray) {
                            expressionList.add(Expr.in(param, (Object[]) value));
                        }
                    default:
                        if (isString) {
                            if (ignoreCase) {
                                expressionList.add(Expr.icontains(param, (String) value));
                            } else {
                                expressionList.add(Expr.contains(param, (String) value));
                            }
                        } else {
                            expressionList.add(Expr.eq(param, value));
                        }
                        break;
                }

            }
        }
        return expressionList;
    }

    /**
     * Return a ExpressionList specifying propertyName contains value.
     *
     * @param expressionList the ExpressionList to add contains expression
     * @param propertyName   the property name of entity bean.
     * @param value          contains value, like %value%.
     * @param <T>            the type of entity.
     * @return a ExpressionList specifying propertyName contains value.
     */
    public static <T> ExpressionList<T> containsIfNoneBlank(ExpressionList<T> expressionList,
                                                            String propertyName,
                                                            String value) {
        Assert.notNull(expressionList, "expressionList must not null");
        Assert.hasText(propertyName, "propertyName must not blank");
        if (StringUtils.hasText(value)) {
            return expressionList.contains(propertyName, value);
        }
        return expressionList;
    }

    /**
     * Return a ExpressionList specifying propertyName equals value.
     *
     * @param expressionList the ExpressionList to add contains expression
     * @param propertyName   the property name of entity bean.
     * @param value          equals value
     * @param <T>            the type of entity.
     * @return a ExpressionList specifying propertyName equals value.
     */
    public static <T> ExpressionList<T> eqIfNotNull(ExpressionList<T> expressionList,
                                                    String propertyName,
                                                    Object value) {
        Assert.notNull(expressionList, "expressionList must not null");
        Assert.hasText(propertyName, "propertyName must not null");
        if (value != null) {
            return expressionList.eq(propertyName, value);
        }
        return expressionList;
    }

    /**
     * Return a ExpressionList specifying propertyName between start and end.
     *
     * @param expressionList the ExpressionList to add contains expression
     * @param propertyName   the property name of entity bean.
     * @param start          start value.
     * @param end            end value.
     * @param <T>            the type of entity.
     * @return a ExpressionList specifying propertyName between start and end.
     */
    public static <T> ExpressionList<T> betweenIfNotNull(ExpressionList<T> expressionList,
                                                         String propertyName,
                                                         Object start,
                                                         Object end) {
        Assert.notNull(expressionList, "expressionList must not null");
        Assert.hasText(propertyName, "propertyName must not null");
        if (start != null && end != null) {
            return expressionList.between(propertyName, start, end);
        }
        return expressionList;
    }

    /**
     * Return a ExpressionList specifying propertyNames contains value.
     *
     * @param expressionList the ExpressionList to add contains expression
     * @param propertyNames  the property name of entity bean.
     * @param value          contains value.
     * @param <T>            the type of entity.
     * @return the ExpressionList specifying propertyNames contains value.
     */
    public static <T> ExpressionList<T> orContains(ExpressionList<T> expressionList,
                                                   List<String> propertyNames,
                                                   String value) {
        Assert.notNull(expressionList, "expressionList must not null");
        Assert.notEmpty(propertyNames, "propertyNames must not empty");
        if (StringUtils.hasText(value)) {
            Junction<T> junction = expressionList
                    .or();
            ExpressionList<T> exp = null;
            for (String propertyName : propertyNames) {
                if (exp == null) {
                    exp = junction.contains(propertyName, value);
                } else {
                    exp = exp.contains(propertyName, value);
                }
            }
            if (exp != null) {
                exp.endOr();
                return exp;
            }
        }
        return expressionList;
    }

    /**
     * Return query specifying page.
     *
     * @param expressionList the ExpressionList to add contains expression
     * @param pageable       0-based index page.
     * @param <T>            the type of entity.
     * @return the query specifying page.
     */
    public static <T> Query<T> queryWithPage(ExpressionList<T> expressionList, Pageable pageable) {
        Assert.notNull(expressionList, "expressionList must not null");
        Assert.notNull(pageable, "pageable must not null");
        return expressionList.setMaxRows(pageable.getPageSize())
                .setFirstRow((int) pageable.getOffset())
                .setOrder(Converters.convertToEbeanOrderBy(pageable.getSort()));
    }

    private static <T> Query<T> query(EbeanServer ebeanServer, Class<T> entityType,
                                      String fetchPath, Object queryObject, Pageable pageable) {
        Assert.notNull(ebeanServer, "ebeanServer must not null");
        Assert.notNull(entityType, "entityType must not null");
        Query<T> query = ebeanServer.find(entityType);
        if (StringUtils.hasText(fetchPath)) {
            query.apply(PathProperties.parse(fetchPath));
        } else {
            // queryObject IncludePath
            if (queryObject != null) {
                if (queryObject.getClass().isAnnotationPresent(IncludeFields.class)) {
                    IncludeFields includeFields = queryObject.getClass().getAnnotation(IncludeFields.class);
                    String ifs = includeFields.value();
                    if (StringUtils.hasText(ifs)) {
                        query.apply(PathProperties.parse(ifs));
                    }
                }
            }
        }

        ExpressionList<T> expressionList = query.where();
        applyWhere(expressionList, queryObject);
        if (pageable != null) {
            expressionList.setMaxRows(pageable.getPageSize())
                    .setFirstRow((int) pageable.getOffset())
                    .setOrder(Converters.convertToEbeanOrderBy(pageable.getSort()));
        }
        return expressionList.query();
    }

    /**
     * Return an object relational query for finding a List, Set, Map or single entity bean.
     *
     * @return the Query.
     */
    @Override
    public <T> Query<T> createQuery(Class<T> entityType, Object queryObject) {
        Assert.notNull(entityType, "entityType must not null");
        Assert.notNull(queryObject, "queryObject must not null");
        return query(entityType, queryObject);
    }

    /**
     * Return an object relational query for finding a List, Set, Map or single entity bean.
     *
     * @return the Query.
     */
    @Override
    public <T> Query<T> createQuery(Class<T> entityType, Object queryObject, Pageable pageable) {
        Assert.notNull(entityType, "entityType must not null");
        Assert.notNull(queryObject, "queryObject must not null");
        return query(entityType, queryObject, pageable);
    }

    /**
     * Return an object relational query for finding a List, Set, Map or single entity bean.
     *
     * @return the Query.
     */
    @Override
    public <T> Query<T> createQuery(Class<T> entityType) {
        Assert.notNull(entityType, "entityType must not null");
        return ebeanServer.find(entityType);
    }

    /**
     * Return a query using Ebean ORM query.
     *
     * @param eql the Ebean ORM query.
     * @return the created Query using ORM query.
     */
    @Override
    public <T> Query<T> createQuery(Class<T> entityType, String eql) {
        Assert.notNull(entityType, "entityType must not null");
        Assert.hasText(eql, "eql must has text");
        return ebeanServer.createQuery(entityType, eql);
    }

    /**
     * Return an SqlQuery for performing native SQL queries that return SqlRow's.
     *
     * @param sql the sql to create SqlQuery using native SQL.
     * @return the created SqlQuery.
     */
    @Override
    public SqlQuery createSqlQuery(String sql) {
        Assert.hasText(sql, "sql must has text");
        return ebeanServer.createSqlQuery(sql);
    }

    /**
     * Return a query using native SQL.
     *
     * @param sql native SQL.
     * @return the created Query using native SQL.
     */
    @Override
    public <T> Query<T> createSqlQuery(Class<T> entityType, String sql) {
        Assert.notNull(entityType, "entityType must not null");
        Assert.hasText(sql, "sql must has text");
        RawSqlBuilder rawSqlBuilder = RawSqlBuilder.parse(sql);
        return ebeanServer.find(entityType).setRawSql(rawSqlBuilder.create());
    }

    /**
     * Return a query using native SQL and column mapping.
     *
     * @param sql           native SQL
     * @param columnMapping column mapping,key is dbColumn, value is propertyName.
     * @return the created Query using native SQL and column mapping config.
     */
    @Override
    public <T> Query<T> createSqlQueryMappingColumns(Class<T> entityType,
                                                     String sql,
                                                     Map<String, String> columnMapping) {
        Assert.notNull(entityType, "entityType must not null");
        Assert.hasText(sql, "sql must has text");
        Assert.notEmpty(columnMapping, "columnMapping must not empty");
        RawSqlBuilder rawSqlBuilder = RawSqlBuilder.parse(sql);
        columnMapping.entrySet().forEach(entry -> {
            rawSqlBuilder.columnMapping(entry.getKey(), entry.getValue());
        });
        return ebeanServer.find(entityType).setRawSql(rawSqlBuilder.create());
    }

    /**
     * Return a query using native SQL and column mapping.
     *
     * @param sql               native SQL
     * @param tableAliasMapping table alias mapping,key is tableAlias, value is propertyName.
     * @return the created Query using native SQL and column mapping config
     */
    @Override
    public <T> Query<T> createSqlQueryMappingTableAlias(Class<T> entityType,
                                                        String sql,
                                                        Map<String, String> tableAliasMapping) {
        Assert.notNull(entityType, "entityType must not null");
        Assert.hasText(sql, "sql must has text");
        Assert.notEmpty(tableAliasMapping, "tableAliasMapping must not empty");
        RawSqlBuilder rawSqlBuilder = RawSqlBuilder.parse(sql);
        tableAliasMapping.entrySet().forEach(entry -> {
            rawSqlBuilder.tableAliasMapping(entry.getKey(), entry.getValue());
        });
        return ebeanServer.find(entityType).setRawSql(rawSqlBuilder.create());
    }

    /**
     * Return a query using query name.
     *
     * @param queryName the name of query defined in ebean.xml or Entity.
     * @return the query using query name.
     */
    @Override
    public <T> Query<T> createNamedQuery(Class<T> entityType, String queryName) {
        return ebeanServer.createNamedQuery(entityType, queryName);
    }

    /**
     * Return a dto query using sql.
     *
     * @param dtoType DTO Bean type, just normal classes
     * @param sql     native SQL
     * @return
     */
    @Override
    public <T> DtoQuery<T> createDtoQuery(Class<T> dtoType, String sql) {
        return ebeanServer.findDto(dtoType, sql).setRelaxedMode();
    }

    /**
     * Return a named dto query.
     *
     * @param dtoType    DTO Bean type, just normal classes
     * @param namedQuery the query using query name.
     * @return
     */
    @Override
    public <T> DtoQuery<T> createNamedDtoQuery(Class<T> dtoType, String namedQuery) {
        return ebeanServer.createNamedDtoQuery(dtoType, namedQuery).setRelaxedMode();
    }

    /**
     * Return a ExampleExpression using example.
     *
     * @return the created ExampleExpression using example
     */
    @Override
    public ExampleExpression exampleOf(Object example) {
        return ebeanServer.getExpressionFactory().exampleLike(example);
    }

    /**
     * Return a ExampleExpression specifying more options.
     *
     * @return the created ExampleExpression specifying more options.
     */
    @Override
    public ExampleExpression exampleOf(Object example,
                                       boolean caseInsensitive,
                                       LikeType likeType) {
        return ebeanServer.getExpressionFactory().exampleLike(example, caseInsensitive, likeType);
    }
}
