package com.pixurvival.contentPackEditor.component.constraint;

import java.awt.image.BufferedImage;
import java.util.function.Predicate;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.core.contentPack.validation.handler.UnitSpriteSheetHandler;

public class UnitSpriteSheetConstraint implements Predicate<ResourceEntry> {

	@Override
	public boolean test(ResourceEntry entry) {
		if (entry == null || !(entry.getPreview() instanceof BufferedImage)) {
			return true;
		}

		return UnitSpriteSheetHandler.test((BufferedImage) entry.getPreview());
	}

}
