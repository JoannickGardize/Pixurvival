package com.pixurvival.core.contentPack.validation.handler;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.annotation.ElementList;
import com.pixurvival.core.reflection.visitor.VisitNode;

public class ElementListHandler implements AnnotationHandler {

	@Override
	public Collection<Class<? extends Annotation>> getHandledAnnotations() {
		return Collections.singleton(ElementList.class);
	}

	@Override
	public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {
		List<?> list = (List<?>) node.getObject();
		for (int i = 0; i < list.size(); i++) {
			Object element = list.get(i);
			if (((IdentifiedElement) element).getId() != i) {
				errors.add(node, annotation);
			}
		}
	}
}
