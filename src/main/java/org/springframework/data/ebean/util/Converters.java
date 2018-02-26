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

package org.springframework.data.ebean.util;

import io.ebean.OrderBy;
import io.ebean.PagedList;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

/**
 * Ebean PageList and Order util to or from Spring data Page or Sort.
 *
 * @author Xuegui Yuan
 */
public class Converters {

  /**
   * Convert spring data Sort to Ebean OrderBy.
   *
   * @param sort
   * @param <T>
   * @return
   */
  public static <T> OrderBy<T> convertToEbeanOrderBy(Sort sort) {
    if (sort == null) {
      return null;
    }
    List<String> list = new ArrayList<>();
    while (sort.iterator().hasNext()) {
      Sort.Order so = sort.iterator().next();
      list.add(so.getDirection() == Sort.Direction.ASC ? so.getProperty() + " asc" : so.getProperty() + " desc");
    }
    return new OrderBy<T>(StringUtils.collectionToCommaDelimitedString(list));
  }

  /**
   * Convert Ebean pagedList Sort to Spring data Page.
   *
   * @param pagedList
   * @param sort
   * @param <T>
   * @return
   */
  public static <T> Page<T> convertToSpringDataPage(PagedList<T> pagedList, Sort sort) {
    return new PageImpl<T>(pagedList.getList(),
        new PageRequest(pagedList.getPageIndex(), pagedList.getPageSize(), sort),
        // PageRequest.of(pagedList.getPageIndex(), pagedList.getPageSize(), sort),
            pagedList.getTotalCount());
  }
}
