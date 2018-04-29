package org.springframework.data.ebean.annotation;

import org.springframework.data.ebean.querychannel.ExprType;

import java.lang.annotation.*;

/**
 * Expr param.
 *
 * @author XueguiYuan
 * @version 1.0 (created time: 2018/4/29).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ExprParam {

    /**
     * Query param name.
     */
    String name() default "";

    /**
     * Query param name.
     */
    String value() default "";

    /**
     * Expr.
     */
    ExprType expr() default ExprType.DEFAULT;

    /**
     * Case insensitive.
     */
    boolean ignoreCase() default true;

    /**
     * If true, do nothing when field value is null, else and expr isNull.
     */
    boolean escapeNull() default true;
}
