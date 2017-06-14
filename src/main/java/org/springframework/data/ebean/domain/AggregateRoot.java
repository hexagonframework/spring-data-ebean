package org.springframework.data.ebean.domain;

import java.lang.annotation.*;

/**
 * Annotation placed on types that should be treated as the root of an aggregate.
 *
 * @author Xuegui Yuan
 */
@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AggregateRoot {
    /**
     * Get the String representation of the aggregate's type. Optional. This defaults to the simple name of the
     * annotated class.
     */
    String type() default "";
}
