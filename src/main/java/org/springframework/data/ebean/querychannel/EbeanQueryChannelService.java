package org.springframework.data.ebean.querychannel;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.ExampleExpression;
import io.ebean.ExpressionList;
import io.ebean.Junction;
import io.ebean.LikeType;
import io.ebean.Query;
import io.ebean.RawSqlBuilder;
import io.ebean.SqlQuery;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.data.ebean.convert.PageListOrderConverter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Query channel service class.
 *
 * @author Xuegui Yuan
 */
public class EbeanQueryChannelService {

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
   * Return an object relational query for finding a List, Set, Map or single entity bean.
   *
   * @return the Query.
   */
  public static <T> Query<T> query(Class<T> entityType) {
    Assert.notNull(entityType, "entityType must not null");
    return Ebean.find(entityType);
  }

  /**
   * Return a ExpressionList specifying propertyName contains value.
   *
   * @param expressionList the ExpressionList to add contains expression
   * @param propertyName the property name of entity bean.
   * @param value contains value, like %value%.
   * @param <T> the type of entity.
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
   * @param propertyName the property name of entity bean.
   * @param value equals value
   * @param <T> the type of entity.
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
   * @param propertyName the property name of entity bean.
   * @param start start value.
   * @param end end value.
   * @param <T> the type of entity.
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
   * @param propertyNames the property name of entity bean.
   * @param value contains value.
   * @param <T> the type of entity.
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
      exp.endOr();
      return exp;
    }
    return expressionList;
  }

  /**
   * Return query specifying page.
   *
   * @param expressionList the ExpressionList to add contains expression
   * @param pageable       0-based index page.
   * @param <T> the type of entity.
   * @return the query specifying page.
   */
  public static <T> Query<T> queryWithPage(ExpressionList<T> expressionList, Pageable pageable) {
    Assert.notNull(expressionList, "expressionList must not null");
    Assert.notNull(pageable, "pageable must not null");
    return expressionList.setMaxRows(pageable.getPageSize())
        .setFirstRow(pageable.getOffset())
        .setOrder(PageListOrderConverter.convertToEbeanOrder(pageable.getSort()));
  }

  /**
   * Return an object relational query for finding a List, Set, Map or single entity bean.
   *
   * @return the Query.
   */
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
  public <T> Query<T> createNamedQuery(Class<T> entityType, String queryName) {
    return ebeanServer.createNamedQuery(entityType, queryName);
  }

  /**
   * Return a ExampleExpression using example.
   *
   * @return the created ExampleExpression using example
   */
  public ExampleExpression exampleOf(Object example) {
    return ebeanServer.getExpressionFactory().exampleLike(example);
  }

  /**
   * Return a ExampleExpression specifying more options.
   *
   * @return the created ExampleExpression specifying more options.
   */
  public ExampleExpression exampleOf(Object example,
                                     boolean caseInsensitive,
                                     LikeType likeType) {
    return ebeanServer.getExpressionFactory().exampleLike(example, caseInsensitive, likeType);
  }
}
