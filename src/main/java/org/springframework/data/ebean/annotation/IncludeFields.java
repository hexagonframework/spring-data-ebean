package org.springframework.data.ebean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define the fetch path string for query select and fetch.
 *
 * @author XueguiYuan
 * @version 1.0 (created time: 2018/4/29).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IncludeFields {
    /**
     * Query fetch path string.
     */
    String value();
}
