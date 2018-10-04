package com.guichang.starter.glog.annotation;


import java.lang.annotation.*;

/**
 * 模块标注
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModuleLog {
    /**
     * 模块名
     */
    String value() default "未命名模块";
}
