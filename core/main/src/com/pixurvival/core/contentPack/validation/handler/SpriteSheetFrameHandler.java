package com.pixurvival.core.contentPack.validation.handler;

import java.awt.image.BufferedImage;
import java.lang.annotation.Annotation;

import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.ImageAccessor;
import com.pixurvival.core.reflection.visitor.VisitNode;

public abstract class SpriteSheetFrameHandler implements AnnotationHandler {

	private ImageAccessor imageAccessor;

	@Override
	public void begin(ImageAccessor imageAccessor) {
		this.imageAccessor = imageAccessor;
	}

	@Override
	public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {
		SpriteSheet spriteSheet = (SpriteSheet) node.getParent().getObject();
		BufferedImage image = imageAccessor.get(spriteSheet.getImage());
		if (image == null || image.getWidth() % spriteSheet.getWidth() != 0 || image.getHeight() % spriteSheet.getHeight() != 0) {
			return;
		}
		int frameX = image.getWidth() / spriteSheet.getWidth();
		int frameY = image.getHeight() / spriteSheet.getHeight();
		handle(node, annotation, frameX, frameY, errors);
	}

	public abstract void handle(VisitNode node, Annotation annotation, int frameX, int frameY, ErrorCollection errors);

}
