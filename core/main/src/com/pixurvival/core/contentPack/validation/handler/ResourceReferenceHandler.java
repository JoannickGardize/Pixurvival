package com.pixurvival.core.contentPack.validation.handler;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.annotation.ResourceReference;
import com.pixurvival.core.reflection.visitor.VisitNode;

public class ResourceReferenceHandler implements AnnotationHandler {

	@Override
	public Collection<Class<? extends Annotation>> getHandledAnnotations() {
		return Collections.singleton(ResourceReference.class);
	}

	@Override
	public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {
		ResourceReference resourceReference = (ResourceReference) annotation;
		ContentPack contentPack = (ContentPack) node.getRoot().getObject();
		if (!contentPack.containsResource((String) node.getObject())) {
			errors.add(node, resourceReference);
		}
	}

}
