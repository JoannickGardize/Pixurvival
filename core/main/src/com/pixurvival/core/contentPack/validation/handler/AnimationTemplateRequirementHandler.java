package com.pixurvival.core.contentPack.validation.handler;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.annotation.AnimationTemplateRequirement;
import com.pixurvival.core.reflection.visitor.VisitNode;

public class AnimationTemplateRequirementHandler implements AnnotationHandler {

	@Override
	public Collection<Class<? extends Annotation>> getHandledAnnotations() {
		return Collections.singleton(AnimationTemplateRequirement.class);
	}

	@Override
	public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {
		AnimationTemplateRequirement requirement = (AnimationTemplateRequirement) annotation;
		AnimationTemplate template = ((SpriteSheet) node.getObject()).getAnimationTemplate();
		if (template != null && !requirement.value().test(template.getAnimations().keySet())) {
			errors.add(node, annotation);
		}
	}
}
