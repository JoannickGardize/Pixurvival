package com.pixurvival.core.contentPack.validation.handler;

import java.awt.image.BufferedImage;
import java.lang.annotation.Annotation;

import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.ImageAccessor;
import com.pixurvival.core.reflection.visitor.VisitNode;

public abstract class SpriteDimensionHandler implements AnnotationHandler {

	private ImageAccessor imageAccessor;

	@Override
	public void begin(ImageAccessor imageAccessor) {
		this.imageAccessor = imageAccessor;
	}

	@Override
	public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {
		SpriteSheet spriteSheet = (SpriteSheet) node.getParent().getObject();
		BufferedImage image = imageAccessor.get(spriteSheet.getImage());
		if (image == null) {
			return;
		}
		int spriteSize = (int) node.getObject();
		int imageSize = getDimension(image);
		if (spriteSize <= 0 || spriteSize > imageSize || imageSize % spriteSize != 0) {
			errors.add(node, annotation);
		}
	}

	protected abstract int getDimension(BufferedImage image);

}
