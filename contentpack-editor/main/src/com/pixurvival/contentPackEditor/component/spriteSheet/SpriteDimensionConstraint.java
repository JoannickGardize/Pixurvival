package com.pixurvival.contentPackEditor.component.spriteSheet;

import java.awt.image.BufferedImage;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SpriteDimensionConstraint implements Predicate<Number> {

	private Supplier<SpriteSheet> spriteSheetSupplier;
	private ToIntFunction<BufferedImage> dimensionGetter;

	@Override
	public boolean test(Number value) {
		SpriteSheet spriteSheet = spriteSheetSupplier.get();
		if (spriteSheet == null) {
			return true;
		}
		ResourceEntry resourceEntry = ResourcesService.getInstance().getResource(spriteSheet.getImage());
		if (resourceEntry == null) {
			return true;
		}
		if (!(resourceEntry.getPreview() instanceof BufferedImage)) {
			return true;
		}
		int spriteSize = value.intValue();
		int imageSize = dimensionGetter.applyAsInt((BufferedImage) resourceEntry.getPreview());
		return spriteSize > 0 && spriteSize <= imageSize && imageSize % spriteSize == 0;
	}
}
