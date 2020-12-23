package com.pixurvival.core.contentPack.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ElementList {

	// TODO remove and use generics reflection
	Class<? extends NamedIdentifiedElement> value();
}