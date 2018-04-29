package org.springframework.data.ebean.querychannel;

import io.ebean.*;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * @author XueguiYuan
 * @version 1.0 (created time: 2018/4/29).
 */
public interface QueryChannelService {

    <T> Query<T> createQuery(Class<T> entityType, Object queryObject);

    <T> Query<T> createQuery(Class<T> entityType, Object queryObject, Pageable pageable);

    <T> Query<T> createQuery(Class<T> entityType);

    <T> Query<T> createQuery(Class<T> entityType, String eql);

    SqlQuery createSqlQuery(String sql);

    <T> Query<T> createSqlQuery(Class<T> entityType, String sql);

    <T> Query<T> createSqlQueryMappingColumns(Class<T> entityType,
                                              String sql,
                                              Map<String, String> columnMapping);

    <T> Query<T> createSqlQueryMappingTableAlias(Class<T> entityType,
                                                 String sql,
                                                 Map<String, String> tableAliasMapping);

    <T> Query<T> createNamedQuery(Class<T> entityType, String queryName);

    <T> DtoQuery<T> createDtoQuery(Class<T> dtoType, String sql);

    <T> DtoQuery<T> createNamedDtoQuery(Class<T> dtoType, String namedQuery);

    ExampleExpression exampleOf(Object example);

    ExampleExpression exampleOf(Object example,
                                boolean caseInsensitive,
                                LikeType likeType);
}
