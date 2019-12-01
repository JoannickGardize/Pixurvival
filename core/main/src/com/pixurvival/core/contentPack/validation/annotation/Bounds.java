package com.pixurvival.core.contentPack.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bounds {

	float min() default Float.NEGATIVE_INFINITY;

	float max() default Float.POSITIVE_INFINITY;

	boolean minInclusive() default true;

	boolean maxInclusive() default false;
}
