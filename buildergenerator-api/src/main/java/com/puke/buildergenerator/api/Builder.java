package com.puke.buildergenerator.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zijiao
 * @version 16/8/22
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.CONSTRUCTOR)
public @interface Builder {

    /**
     * 设置方法前缀
     *
     * @return 前缀
     */
    String value() default "";

}
