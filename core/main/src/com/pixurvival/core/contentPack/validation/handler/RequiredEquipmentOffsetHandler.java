package com.pixurvival.core.contentPack.validation.handler;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.annotation.RequiredEquipmentOffset;
import com.pixurvival.core.reflection.visitor.VisitNode;

public class RequiredEquipmentOffsetHandler implements AnnotationHandler {

	@Override
	public Collection<Class<? extends Annotation>> getHandledAnnotations() {
		return Collections.singleton(RequiredEquipmentOffset.class);
	}

	@Override
	public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {
		if (node.getObject() != null && ((SpriteSheet) node.getObject()).getEquipmentOffset() == null) {
			errors.add(node, annotation);
		}
	}

}
