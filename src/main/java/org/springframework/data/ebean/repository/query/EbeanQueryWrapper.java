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

import io.ebean.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.data.util.StreamUtils;

import java.util.List;
import java.util.stream.Stream;

/**
 * Ebean query wrapper, wrap Query、 SqlQuery、Update、SqlUpdate.
 *
 * @author Xuegui Yuan
 */
public class EbeanQueryWrapper<T> {
  private QueryType queryType;
  private T queryInstance;

  public EbeanQueryWrapper(T queryInstance) {
    this.queryInstance = queryInstance;
    if (queryInstance instanceof Query) {
      this.queryType = QueryType.ORM_QUERY;
    } else if (queryInstance instanceof SqlQuery) {
      this.queryType = QueryType.SQL_QUERY;
    } else if (queryInstance instanceof Update) {
      this.queryType = QueryType.ORM_UPDATE;
    } else if (queryInstance instanceof SqlUpdate) {
      this.queryType = QueryType.SQL_UPDATE;
    } else {
      throw new IllegalArgumentException("query not supported!");
    }
  }

  public static <T> EbeanQueryWrapper ofEbeanQuery(T queryInstance) {
    return new EbeanQueryWrapper(queryInstance);
  }

  public void setParameter(String name, Object value) {
    switch (queryType) {
      case ORM_QUERY:
        ((Query) queryInstance).setParameter(name, value);
        break;
      case SQL_QUERY:
        ((SqlQuery) queryInstance).setParameter(name, value);
        break;
      case ORM_UPDATE:
        ((Update) queryInstance).setParameter(name, value);
        break;
      case SQL_UPDATE:
        ((SqlUpdate) queryInstance).setParameter(name, value);
        break;
      default:
        throw new IllegalArgumentException("query not supported!");
    }
  }

  public void setParameter(int position, Object value) {
    switch (queryType) {
      case ORM_QUERY:
        ((Query) queryInstance).setParameter(position, value);
        break;
      case SQL_QUERY:
        ((SqlQuery) queryInstance).setParameter(position, value);
        break;
      case ORM_UPDATE:
        ((Update) queryInstance).setParameter(position, value);
        break;
      case SQL_UPDATE:
        ((SqlUpdate) queryInstance).setParameter(position, value);
        break;
      default:
        throw new IllegalArgumentException("query not supported!");
    }
  }

  public Object findOne() {
    switch (queryType) {
      case ORM_QUERY:
        return ((Query) queryInstance).findOne();
      case SQL_QUERY:
        return ((SqlQuery) queryInstance).findOne();
      default:
        throw new IllegalArgumentException("query must be Query or SqlQuery!");
    }
  }

  public Page findPage(Pageable pageable) {
    switch (queryType) {
      case ORM_QUERY:
        PagedList pagedList = ((Query) queryInstance)
                .setFirstRow(pageable.getOffset())
                .setMaxRows(pageable.getPageSize())
                .findPagedList();
        return PageableExecutionUtils.getPage(pagedList.getList(), pageable, () -> pagedList.getTotalCount());
      default:
        throw new IllegalArgumentException("query must be Query!");
    }
  }

  public int update() {
    switch (queryType) {
      case ORM_QUERY:
        return ((Query) queryInstance).update();
      case ORM_UPDATE:
        return ((Update) queryInstance).execute();
      case SQL_UPDATE:
        return ((SqlUpdate) queryInstance).execute();
      default:
        throw new IllegalArgumentException("query must be Query or Update or SqlUpdate!");
    }
  }

  public int delete() {
    switch (queryType) {
      case ORM_QUERY:
        return ((Query) queryInstance).delete();
      default:
        throw new IllegalArgumentException("query must be Query!");
    }
  }

  public boolean isExists() {
    switch (queryType) {
      case ORM_QUERY:
        return ((Query) queryInstance).findCount() > 0;
      case SQL_QUERY:
        return ((SqlQuery) queryInstance).findOne().getLong("c") > 0;
      default:
        throw new IllegalArgumentException("query must be Query or SqlQuery!");
    }
  }

  public Stream findStream() {
    switch (queryType) {
      case ORM_QUERY:
        return StreamUtils.createStreamFromIterator(((Query) queryInstance).findIterate());
      default:
        throw new IllegalArgumentException("query must be Query!");
    }
  }

  public Object findList() {
    switch (queryType) {
      case ORM_QUERY:
        return ((Query) queryInstance).findList();
      case SQL_QUERY:
        return ((SqlQuery) queryInstance).findList();
      default:
        throw new IllegalArgumentException("query must be Query or SqlQuery!");
    }
  }

  public Object findSlice(Pageable pageable) {
    List resultList = null;
    int pageSize = pageable.getPageSize();
    int offset = pageable.getOffset();
    switch (queryType) {
      case ORM_QUERY:
        resultList = ((Query) queryInstance).setFirstRow(offset).setMaxRows(pageSize + 1).findList();
        break;
      case SQL_QUERY:
        resultList = ((SqlQuery) queryInstance).setFirstRow(offset).setMaxRows(pageSize + 1).findList();
        break;
      default:
        throw new IllegalArgumentException("query must be Query or SqlQuery!");
    }

    boolean hasNext = resultList != null && resultList.size() > pageSize;
    return new SliceImpl<Object>(hasNext ? resultList.subList(0, pageSize) : resultList, pageable, hasNext);
  }

  public Integer getMaxRows() {
    switch (queryType) {
      case ORM_QUERY:
        return ((Query) queryInstance).getMaxRows();
      default:
        throw new IllegalArgumentException("query must be Query!");
    }
  }

  public void setMaxRows(int maxRows) {
    switch (queryType) {
      case ORM_QUERY:
        ((Query) queryInstance).setMaxRows(maxRows);
        break;
      case SQL_QUERY:
        ((SqlQuery) queryInstance).setMaxRows(maxRows);
        break;
      default:
        throw new IllegalArgumentException("query not supported!");
    }
  }

  public int getFirstRow() {
    switch (queryType) {
      case ORM_QUERY:
        return ((Query) queryInstance).getFirstRow();
      default:
        throw new IllegalArgumentException("query must be Query!");
    }
  }

  public void setFirstRow(int firstRow) {
    switch (queryType) {
      case ORM_QUERY:
        ((Query) queryInstance).setFirstRow(firstRow);
        break;
      case SQL_QUERY:
        ((SqlQuery) queryInstance).setFirstRow(firstRow);
        break;
      default:
        throw new IllegalArgumentException("query not supported!");
    }
  }

  public static enum QueryType {
    /**
     * Query
     */
    ORM_QUERY,
    /**
     * SqlQuery
     */
    SQL_QUERY,
    /**
     * Update
     */
    ORM_UPDATE,
    /**
     * SqlUpdate
     */
    SQL_UPDATE;
  }

}
