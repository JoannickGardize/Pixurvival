package com.pixurvival.core.contentPack.validation.annotation;

import com.pixurvival.core.contentPack.IdentifiedElement;

public @interface ElementCollection {

	Class<? extends IdentifiedElement> value();

	boolean isRoot() default true;

}