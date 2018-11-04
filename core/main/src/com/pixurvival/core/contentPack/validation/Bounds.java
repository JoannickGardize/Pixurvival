package com.pixurvival.core.contentPack.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bounds {

	double min() default Double.NEGATIVE_INFINITY;

	double max() default Double.POSITIVE_INFINITY;

	boolean minInclusive() default true;

	boolean maxInclusive() default true;
}
