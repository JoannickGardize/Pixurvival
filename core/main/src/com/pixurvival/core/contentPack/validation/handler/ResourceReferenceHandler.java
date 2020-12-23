package com.pixurvival.core.contentPack.validation.handler;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.ImageAccessor;
import com.pixurvival.core.contentPack.validation.annotation.ResourceReference;
import com.pixurvival.core.reflection.visitor.VisitNode;

public class ResourceReferenceHandler implements AnnotationHandler {

	private ImageAccessor imageAccessor;

	@Override
	public void begin(ImageAccessor imageAccessor) {
		this.imageAccessor = imageAccessor;
	}

	@Override
	public Collection<Class<? extends Annotation>> getHandledAnnotations() {
		return Collections.singleton(ResourceReference.class);
	}

	@Override
	public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {

		if (imageAccessor.get((String) node.getObject()) == null) {
			errors.add(node, annotation);
		}
	}

}
