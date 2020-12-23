package com.pixurvival.core.contentPack.validation.handler;

import java.awt.image.BufferedImage;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import com.pixurvival.core.contentPack.validation.annotation.SpriteWidth;

public class SpriteWidthHandler extends SpriteDimensionHandler {

	@Override
	public Collection<Class<? extends Annotation>> getHandledAnnotations() {
		return Collections.singleton(SpriteWidth.class);
	}

	@Override
	protected int getDimension(BufferedImage image) {
		return image.getWidth();
	}

}
