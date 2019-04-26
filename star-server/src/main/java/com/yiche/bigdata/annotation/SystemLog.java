package com.yiche.bigdata.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SystemLog {

    /*
     * 业务名称
     * */
    String value() default "";
}
