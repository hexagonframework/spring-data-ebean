package org.springframework.data.ebean.querychannel;

import io.ebean.Ebean;
import io.ebean.ExampleExpression;
import io.ebean.ExpressionList;
import io.ebean.Junction;
import io.ebean.LikeType;
import io.ebean.Query;
import io.ebean.SqlQuery;
import java.util.List;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Query channel service class.
 *
 * @author Xuegui Yuan
 */
public class EbeanQueryChannelService {

  /**
   * Return an object relational query for finding a List, Set, Map or single entity bean.
   *
   * @return the Query.
   */
  public static <T> Query<T> query(Class<T> entityType) {
    return Ebean.find(entityType);
  }

  /**
   * Return a query using OQL.
   *
   * @param oql the Ebean ORM query
   * @return the created Query using ORM query
   */
  public static <T> Query<T> queryWithOql(Class<T> entityType, String oql) {
    return Ebean.createQuery(entityType, oql);
  }

  /**
   * Return a query using native SQL.
   *
   * @param sql native SQL
   * @return the created Query using native SQL
   */
  public static <T> Query<T> queryWithSql(Class<T> entityType, String sql) {
    return Ebean.findNative(entityType, sql);
  }

  /**
   * Return a query using query name.
   *
   * @param queryName
   * @return
   */
  public static <T> Query<T> namedQueryOf(Class<T> entityType, String queryName) {
    return Ebean.createNamedQuery(entityType, queryName);
  }

  /**
   * Return an SqlQuery for performing native SQL queries that return SqlRow's.
   *
   * @param sql the sql to create SqlQuery using native SQL
   * @return the created SqlQuery.
   */
  public static SqlQuery sqlQueryOf(String sql) {
    return Ebean.createSqlQuery(sql);
  }

  /**
   * Return a ExampleExpression using example.
   *
   * @return the created ExampleExpression using example
   */
  public static ExampleExpression exampleOf(Object example) {
    return Ebean.getExpressionFactory().exampleLike(example);
  }

  /**
   * Return a ExampleExpression specifying more options.
   *
   * @return the created ExampleExpression specifying more options
   */
  public static ExampleExpression exampleOf(Object example,
                                            boolean caseInsensitive,
                                            LikeType likeType) {
    return Ebean.getExpressionFactory().exampleLike(example, caseInsensitive, likeType);
  }

  /**
   * Return a ExpressionList specifying propertyName contains value.
   *
   * @param expressionList
   * @param propertyName
   * @param value
   * @param <T>
   * @return
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
   * @param expressionList
   * @param propertyName
   * @param value
   * @param <T>
   * @return
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
   * @param expressionList
   * @param propertyName
   * @param start
   * @param end
   * @param <T>
   * @return
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
   * @param expressionList
   * @param propertyNames
   * @param value
   * @param <T>
   * @return
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

  public static <T> Query<T> queryWithPage(ExpressionList<T> expressionList,
                                           int page,
                                           int pageSize) {  // pageSize is 1-index
    Assert.notNull(expressionList, "expressionList must not null");
    return expressionList.setMaxRows(pageSize)
        .setFirstRow((page - 1) * pageSize);
  }
}
