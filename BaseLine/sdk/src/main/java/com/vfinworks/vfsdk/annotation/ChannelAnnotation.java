package com.vfinworks.vfsdk.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来标示渠道名称
 * inst_code pay_mode 是渠道返回的字符串
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChannelAnnotation {
    String inst_code();
    String pay_mode();
}