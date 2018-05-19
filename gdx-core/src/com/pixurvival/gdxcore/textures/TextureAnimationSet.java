package com.pixurvival.gdxcore.textures;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Texture;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.ContentPackReadException;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.contentPack.sprite.Animation;
import com.pixurvival.core.contentPack.sprite.AnimationTemplate;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class TextureAnimationSet {

	private Map<ActionAnimation, TextureAnimation> map = new EnumMap<>(ActionAnimation.class);
	private @Getter float yOffset;
	private @Getter float width;
	private @Getter float height;
	private @Getter float shadowWidth;
	private @Getter @Setter(AccessLevel.PACKAGE) Texture shadow;

	public TextureAnimationSet(SpriteSheet spriteSheet, PixelTextureBuilder transform) throws ContentPackReadException {
		double truePixelWidth = 1.0 / (transform.getPixelWidth() * GameConstants.PIXEL_PER_UNIT);
		shadowWidth = (float) ((double) spriteSheet.getWidth() / GameConstants.PIXEL_PER_UNIT);
		width = (float) (shadowWidth + truePixelWidth * 2);
		height = (float) ((float) ((double) spriteSheet.getHeight() / GameConstants.PIXEL_PER_UNIT)
				+ truePixelWidth * 2);
		yOffset = (float) -truePixelWidth;

		SpriteSheetPixmap sheetPixmap = new SpriteSheetPixmap(spriteSheet.getImage().read(), spriteSheet.getWidth(),
				spriteSheet.getHeight());
		TextureSheet textureSheet = new TextureSheet(sheetPixmap, transform);

		AnimationTemplate template = spriteSheet.getAnimationTemplate();
		for (Entry<ActionAnimation, Animation> entries : template.getAnimations().entrySet()) {
			Animation animation = entries.getValue();
			TextureAnimation textureAnimation = new TextureAnimation(textureSheet, animation,
					template.getFrameDuration(), spriteSheet.getEquipmentOffset());
			map.put(animation.getAction(), textureAnimation);
		}
	}

	public void put(ActionAnimation action, TextureAnimation animation) {
		map.put(action, animation);
	}

	public TextureAnimation get(ActionAnimation action) {
		return map.get(action);
	}

	public void foreachAnimations(Consumer<TextureAnimation> action) {
		map.values().forEach(action);
	}
}