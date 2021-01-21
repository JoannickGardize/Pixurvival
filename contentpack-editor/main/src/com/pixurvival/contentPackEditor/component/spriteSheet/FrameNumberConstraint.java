package com.pixurvival.contentPackEditor.component.spriteSheet;

import java.awt.image.BufferedImage;
import java.util.function.Predicate;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class FrameNumberConstraint<T extends NamedIdentifiedElement> implements Predicate<T> {

	@FunctionalInterface
	public interface FrameNumberTest<T> {
		boolean test(SpriteSheet spriteSheet, T t, int frameX, int frameY);
	}

	private @NonNull FrameNumberTest<T> test;
	private @Setter @Getter SpriteSheet spriteSheet;

	@Override
	public boolean test(T t) {
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
		if (spriteSheet.getWidth() == 0 || spriteSheet.getHeight() == 0) {
			return true;
		}
		BufferedImage image = (BufferedImage) resourceEntry.getPreview();
		if (image.getWidth() % spriteSheet.getWidth() != 0 || image.getHeight() % spriteSheet.getHeight() != 0) {
			return true;
		}
		int frameX = image.getWidth() / spriteSheet.getWidth();
		int frameY = image.getHeight() / spriteSheet.getHeight();
		return test.test(spriteSheet, t, frameX, frameY);
	}

}
