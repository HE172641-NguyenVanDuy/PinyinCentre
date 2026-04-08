package com.pinyincentre.pinyin.annotation;

import com.pinyincentre.pinyin.constant.RoleType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Permission {
    RoleType[] roles() default {};
    String target() default "Authorization";
    String message() default "Bạn không có quyền thực hiện thao tác này";
}

