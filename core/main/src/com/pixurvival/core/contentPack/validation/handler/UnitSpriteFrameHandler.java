package com.pixurvival.core.contentPack.validation.handler;

import java.awt.image.BufferedImage;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.ImageReferenceHolder;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.ImageAccessor;
import com.pixurvival.core.contentPack.validation.annotation.UnitSpriteFrame;
import com.pixurvival.core.reflection.visitor.VisitNode;

public class UnitSpriteFrameHandler implements AnnotationHandler {

	private ImageAccessor imageAccessor;

	@Override
	public void begin(ImageAccessor imageAccessor) {
		this.imageAccessor = imageAccessor;
	}

	@Override
	public Collection<Class<? extends Annotation>> getHandledAnnotations() {
		return Collections.singleton(UnitSpriteFrame.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {
		if (node.getObject() instanceof Collection) {
			for (Frame frame : (Collection<Frame>) node.getObject()) {
				testFrame(frame, node, annotation, errors);
			}
		} else {
			testFrame((Frame) node.getObject(), node, annotation, errors);
		}

	}

	private void testFrame(Frame frame, VisitNode baseNode, Annotation annotation, ErrorCollection errors) {
		if (!test(frame, imageAccessor.get(((ImageReferenceHolder) baseNode.getParent().getObject()).getImage()))) {
			errors.add(baseNode, annotation);
		}
	}

	public static boolean test(Frame frame, BufferedImage image) {
		if (image == null) {
			return true;
		}
		return frame.getX() < image.getWidth() / GameConstants.PIXEL_PER_UNIT && frame.getY() < image.getHeight() / GameConstants.PIXEL_PER_UNIT;
	}
}
