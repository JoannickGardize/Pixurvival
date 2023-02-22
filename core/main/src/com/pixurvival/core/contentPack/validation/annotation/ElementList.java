package com.pixurvival.core.contentPack.validation.annotation;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ElementList {

    // TODO remove and use generics reflection
    Class<? extends NamedIdentifiedElement> value();
}