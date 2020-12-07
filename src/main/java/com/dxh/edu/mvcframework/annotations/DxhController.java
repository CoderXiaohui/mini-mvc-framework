package com.dxh.edu.mvcframework.annotations;

import java.lang.annotation.*;

/**
 * @Author: Dengxh
 * @Date: 2020/12/6 17:16
 * @Description:
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DxhController {
    String value() default "";

}
