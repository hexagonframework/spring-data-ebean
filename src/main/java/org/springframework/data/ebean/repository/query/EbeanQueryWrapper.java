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

import io.ebean.PagedList;
import io.ebean.Query;
import io.ebean.SqlUpdate;
import io.ebean.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.data.util.StreamUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.data.ebean.repository.query.EbeanQueryWrapper.QueryType.QUERY;

/**
 * Ebean query wrapper, wrap Query、Update、SqlUpdate.
 *
 * @author Xuegui Yuan
 */
public class EbeanQueryWrapper<T> {
  private QueryType queryType;
  private T queryInstance;

  public EbeanQueryWrapper(T queryInstance) {
    this.queryInstance = queryInstance;
    if (queryInstance instanceof Query) {
        this.queryType = QUERY;
    } else if (queryInstance instanceof Update) {
        this.queryType = QueryType.UPDATE;
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
        case QUERY:
        ((Query) queryInstance).setParameter(name, value);
        break;
        case UPDATE:
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
        case QUERY:
        ((Query) queryInstance).setParameter(position, value);
        break;
        case UPDATE:
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
      if (queryType == QUERY) {
          return ((Query) queryInstance).findOne();
    }
      throw new IllegalArgumentException("query not supported!");
  }

  public Page findPage(Pageable pageable) {
      if (queryType == QUERY) {
          PagedList pagedList = ((Query) queryInstance)
                  .setFirstRow((int) pageable.getOffset())
                  .setMaxRows(pageable.getPageSize())
                  .findPagedList();
          return PageableExecutionUtils.getPage(pagedList.getList(), pageable, () -> pagedList.getTotalCount());

    }
      throw new IllegalArgumentException("query not supported!");
  }

  public int update() {
    switch (queryType) {
        case QUERY:
        return ((Query) queryInstance).update();
        case UPDATE:
        return ((Update) queryInstance).execute();
      case SQL_UPDATE:
        return ((SqlUpdate) queryInstance).execute();
      default:
          throw new IllegalArgumentException("query not supported!");
    }
  }

  public int delete() {
    switch (queryType) {
        case QUERY:
        return ((Query) queryInstance).delete();
        case UPDATE:
            return ((Update) queryInstance).execute();
        case SQL_UPDATE:
            return ((SqlUpdate) queryInstance).execute();
      default:
          throw new IllegalArgumentException("query not supported!");
    }
  }

  public boolean isExists() {
      if (queryType == QUERY) {
          return ((Query) queryInstance).findCount() > 0;
    }
      throw new IllegalArgumentException("query not supported!");
  }

  public Stream findStream() {
      if (queryType == QUERY) {
          return StreamUtils.createStreamFromIterator(((Query) queryInstance).findIterate());
    }
      throw new IllegalArgumentException("query not supported!");
  }

  public Object findList() {
      if (queryType == QUERY) {
          return ((Query) queryInstance).findList();
    }
      throw new IllegalArgumentException("query not supported!");
  }

  public Object findSlice(Pageable pageable) {
    List resultList = null;
    int pageSize = pageable.getPageSize();
    int offset = (int)pageable.getOffset();
      if (queryType == QUERY) {
          resultList = ((Query) queryInstance).setFirstRow(offset).setMaxRows(pageSize + 1).findList();
          boolean hasNext = resultList != null && resultList.size() > pageSize;
          return new SliceImpl<Object>(hasNext ? resultList.subList(0, pageSize) : resultList, pageable, hasNext);
      }
      throw new IllegalArgumentException("query not supported!");
  }

  public Integer getMaxRows() {
      if (queryType == QUERY) {
          return ((Query) queryInstance).getMaxRows();
    }
      throw new IllegalArgumentException("query not supported!");
  }

  public void setMaxRows(int maxRows) {
      if (queryType == QUERY) {
          ((Query) queryInstance).setMaxRows(maxRows);
      }
      throw new IllegalArgumentException("query not supported!");
  }

  public int getFirstRow() {
      if (queryType == QUERY) {
          return ((Query) queryInstance).getFirstRow();
    }
      throw new IllegalArgumentException("query not supported!");
  }

  public void setFirstRow(int firstRow) {
      if (queryType == QUERY) {
          ((Query) queryInstance).setFirstRow(firstRow);
      }
      throw new IllegalArgumentException("query not supported!");
  }

  public static enum QueryType {
    /**
     * Query
     */
    QUERY,
    /**
     * Update
     */
    UPDATE,
    /**
     * SqlUpdate
     */
    SQL_UPDATE;
  }

}
