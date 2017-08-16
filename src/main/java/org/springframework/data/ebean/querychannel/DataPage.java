package org.springframework.data.ebean.querychannel;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * 1-based index page.
 *
 * @author Xuegui Yuan
 */
public class DataPage<T> extends PageImpl<T> implements Page<T> {
  /**
   * Constructor of {@code DataPage}.
   *
   * @param content  the content of this page, must not be {@literal null}.
   * @param pageable the paging information, can be {@literal null}.
   * @param total    the total amount of items available. The total might be adapted considering the length of the content
   */
  public DataPage(List<T> content, Pageable pageable, long total) {
    super(content, pageable, total);
  }

  /**
   * UI sent 1-based index page, convert to 0-based index page.
   *
   * @param pageable
   * @return
   */
  public static Pageable pageRequest(Pageable pageable) {
    return new PageRequest(pageable.getPageNumber() - 1,
        pageable.getPageSize(), pageable.getSort());
  }

  @Override
  public boolean hasNext() {
    return getNumber() + 1 < getTotalPages();
  }

  /**
   * Send 1-based index page to UI.
   *
   * @return
   */
  @Override
  public int getNumber() {
    return super.getNumber() + 1;
  }

  @Override
  public boolean hasPrevious() {
    return getNumber() > 1;
  }

}
