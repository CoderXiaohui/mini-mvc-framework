package com.dxh.edu.mvcframework.annotations;

import java.lang.annotation.*;

/**
 * @Author: Dengxh
 * @Date: 2020/12/6 17:18
 * @Description:
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DxhService {
    String value() default "";
}
