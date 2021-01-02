package com.pixurvival.core.contentPack.validation.handler;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

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
		if (!test((Collection<FloatHolder>) node.getObject(), ((Ascending) annotation).lastValue())) {
			errors.add(node, annotation);
		}
	}

	public static boolean test(Collection<FloatHolder> collection, float lastValue) {
		float previousValue = Float.NEGATIVE_INFINITY;
		for (Iterator<FloatHolder> iterator = collection.iterator(); iterator.hasNext();) {
			FloatHolder element = iterator.next();
			if (element.getFloatValue() <= previousValue) {
				return false;
			}
			previousValue = element.getFloatValue();
			if (!iterator.hasNext() && lastValue > Float.NEGATIVE_INFINITY && previousValue != lastValue) {
				return false;
			}
		}
		return true;
	}
}
