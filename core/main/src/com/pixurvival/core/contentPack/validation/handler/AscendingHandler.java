package com.pixurvival.core.contentPack.validation.handler;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import com.pixurvival.core.contentPack.FloatHolder;
import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.annotation.Ascending;
import com.pixurvival.core.reflection.visitor.VisitNode;

public class AscendingHandler implements AnnotationHandler {

	@Override
	public Collection<Class<? extends Annotation>> getHandledAnnotations() {
		return Collections.singleton(Ascending.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {
		if (!test((Collection<FloatHolder>) node.getObject())) {
			errors.add(node, annotation);
		}
	}

	public static boolean test(Collection<FloatHolder> collection) {
		float previousValue = Float.NEGATIVE_INFINITY;
		for (FloatHolder element : collection) {
			if (element.getFloatValue() <= previousValue) {
				return false;
			}
			previousValue = element.getFloatValue();
		}
		return true;
	}
}
