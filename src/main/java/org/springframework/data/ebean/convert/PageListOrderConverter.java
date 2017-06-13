package org.springframework.data.ebean.convert;

import io.ebean.OrderBy;
import io.ebean.PagedList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Ebean PageList and Order convert to or from Spring data Page or Sort.
 *
 * @author Xuegui Yuan
 */
public class PageListOrderConverter {

    /**
     * Convert spring data Sort to Ebean OrderBy.
     *
     * @param sort
     * @param <T>
     * @return
     */
    public static <T> OrderBy<T> convertToEbeanOrder(Sort sort) {
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
                pagedList.getTotalCount());
    }
}
