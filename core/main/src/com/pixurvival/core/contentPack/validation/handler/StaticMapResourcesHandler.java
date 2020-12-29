package com.pixurvival.core.contentPack.validation.handler;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import com.pixurvival.core.contentPack.map.StaticMapProvider;
import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.ImageAccessor;
import com.pixurvival.core.contentPack.validation.annotation.StaticMapResources;
import com.pixurvival.core.reflection.visitor.VisitNode;

public class StaticMapResourcesHandler implements AnnotationHandler {

	private ImageAccessor imageAccessor;

	@Override
	public void begin(ImageAccessor imageAccessor) {
		this.imageAccessor = imageAccessor;
	}

	@Override
	public Collection<Class<? extends Annotation>> getHandledAnnotations() {
		return Collections.singleton(StaticMapResources.class);
	}

	@Override
	public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {
		StaticMapProvider map = (StaticMapProvider) node.getObject();
		testResource(node, errors, map.getStructuresImageResourceName());
		testResource(node, errors, map.getTilesImageResourceName());
	}

	private void testResource(VisitNode node, ErrorCollection errors, String resourceName) {
		if (imageAccessor.get(resourceName) == null) {
			errors.add(node, new StaticMapResourceMissing(resourceName));
		}
	}
}
