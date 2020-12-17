package com.pixurvival.core.contentPack.validation.handler;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.reflection.visitor.VisitNode;

public class PositiveHandler implements AnnotationHandler {

	@Override
	public Collection<Class<? extends Annotation>> getHandledAnnotations() {
		return Collections.singleton(Positive.class);
	}

	@Override
	public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {
		if (((Number) node.getObject()).floatValue() < 0) {
			errors.add(node, annotation);
		}
	}
}
