package com.pixurvival.core.contentPack.validation.handler;

import java.awt.image.BufferedImage;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.ImageAccessor;
import com.pixurvival.core.contentPack.validation.annotation.UnitSpriteSheet;
import com.pixurvival.core.reflection.visitor.VisitNode;

public class UnitSpriteSheetHandler implements AnnotationHandler {

	private ImageAccessor imageAccessor;

	@Override
	public void begin(ImageAccessor imageAccessor) {
		this.imageAccessor = imageAccessor;
	}

	@Override
	public Collection<Class<? extends Annotation>> getHandledAnnotations() {
		return Collections.singleton(UnitSpriteSheet.class);
	}

	@Override
	public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {
		if (!test(imageAccessor.get((String) node.getObject()))) {
			errors.add(node, annotation);
		}
	}

	public static boolean test(BufferedImage image) {
		return image.getWidth() % GameConstants.PIXEL_PER_UNIT == 0 && image.getHeight() % GameConstants.PIXEL_PER_UNIT == 0;
	}
}
