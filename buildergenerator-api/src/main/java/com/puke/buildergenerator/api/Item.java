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
@Target(ElementType.PARAMETER)
public @interface Item {

    /**
     * 设置映射属性名
     *
     * @return 映射名
     */
    String value() default "";

}
