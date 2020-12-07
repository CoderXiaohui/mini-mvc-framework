package com.dxh.edu.mvcframework.annotations;

import java.lang.annotation.*;

/**
 * @Author: Dengxh
 * @Date: 2020/12/6 17:18
 * @Description:
 */
@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DxhRequestMapping {
    String value() default "";
}
