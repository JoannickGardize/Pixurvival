package com.pixurvival.core.contentPack.validation.handler;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.FrameOffset;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.annotation.EquipmentOffsetFrames;
import com.pixurvival.core.reflection.visitor.VisitNode;

public class EquipmentOffsetFramesHandler extends SpriteSheetFrameHandler {

	@Override
	public Collection<Class<? extends Annotation>> getHandledAnnotations() {
		return Collections.singleton(EquipmentOffsetFrames.class);
	}

	@Override
	public void handle(VisitNode node, Annotation annotation, int frameX, int frameY, ErrorCollection errors) {
		if (!test((SpriteSheet) node.getParent().getObject(), frameX, frameY)) {
			errors.add(node, annotation);
		}
	}

	public static boolean test(SpriteSheet spriteSheet, int frameX, int frameY) {
		if (spriteSheet.getEquipmentOffset() == null) {
			return true;
		}
		Set<Frame> frameSet = new HashSet<>();
		for (FrameOffset frameOffset : spriteSheet.getEquipmentOffset().getFrameOffsets()) {
			if (frameOffset.getX() >= frameX || frameOffset.getY() >= frameY || frameOffset.getOffsetX() >= spriteSheet.getWidth() || frameOffset.getOffsetY() >= spriteSheet.getHeight()) {
				return false;
			}
			frameSet.add(frameOffset);
		}
		if (frameSet.size() != frameX * frameY) {
			return false;
		}
		return true;
	}
}
