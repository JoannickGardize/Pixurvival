package com.pixurvival.core.contentPack.validation.annotation;

import com.pixurvival.core.contentPack.FloatHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for a list of {@link FloatHolder} who must be in ascending order.
 *
 * @author SharkHendrix
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Ascending {
    float lastValue() default Float.NEGATIVE_INFINITY;
}
