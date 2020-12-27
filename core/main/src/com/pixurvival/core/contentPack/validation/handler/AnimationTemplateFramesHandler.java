package com.pixurvival.core.contentPack.validation.handler;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import com.pixurvival.core.contentPack.sprite.Animation;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.annotation.AnimationTemplateFrames;
import com.pixurvival.core.reflection.visitor.VisitNode;

public class AnimationTemplateFramesHandler extends SpriteSheetFrameHandler {

	@Override
	public Collection<Class<? extends Annotation>> getHandledAnnotations() {
		return Collections.singleton(AnimationTemplateFrames.class);
	}

	@Override
	public void handle(VisitNode node, Annotation annotation, int frameX, int frameY, ErrorCollection errors) {
		if (!test((AnimationTemplate) node.getObject(), frameX, frameY)) {
			errors.add(node, annotation);
		}
	}

	public static boolean test(AnimationTemplate animationTemplate, int frameX, int frameY) {
		for (Animation animation : animationTemplate.getAnimations().values()) {
			for (Frame frame : animation.getFrames()) {
				if (frame.getX() >= frameX || frame.getY() >= frameY) {
					return false;
				}
			}
		}
		return true;
	}

}
