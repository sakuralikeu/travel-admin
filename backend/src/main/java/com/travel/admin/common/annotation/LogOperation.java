package com.travel.admin.common.annotation;

import com.travel.admin.common.enums.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogOperation {

    String module();

    String name();

    OperationType type() default OperationType.OTHER;
}
