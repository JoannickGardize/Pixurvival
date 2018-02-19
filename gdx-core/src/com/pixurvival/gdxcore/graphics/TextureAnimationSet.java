package com.pixurvival.gdxcore.graphics;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ActionAnimation;
import com.pixurvival.core.contentPack.Animation;
import com.pixurvival.core.contentPack.AnimationTemplate;
import com.pixurvival.core.contentPack.ContentPackReadException;
import com.pixurvival.core.contentPack.SpriteSheet;

import lombok.Getter;

public class TextureAnimationSet {

	private Map<ActionAnimation, TextureAnimation> map = new HashMap<>();
	private @Getter float xOffset;
	private @Getter float yOffset;

	public TextureAnimationSet(SpriteSheet spriteSheet, PixelTextureBuilder transform) throws ContentPackReadException {
		float truePixelWidth = 1f / transform.getPixelWidth() * World.PIXEL_PER_UNIT;
		xOffset = truePixelWidth + (float) spriteSheet.getWidth() / World.PIXEL_PER_UNIT / 2f;
		yOffset = truePixelWidth;
		SpriteSheetPixmap sheetPixmap = new SpriteSheetPixmap(spriteSheet.getImage().read(), spriteSheet.getWidth(),
				spriteSheet.getHeight());
		TextureSheet textureSheet = new TextureSheet(sheetPixmap, transform);

		AnimationTemplate template = spriteSheet.getAnimationTemplate();
		for (Entry<ActionAnimation, Animation> entries : template.getAnimations().entrySet()) {
			Animation animation = entries.getValue();
			TextureAnimation textureAnimation = new TextureAnimation(textureSheet, animation,
					template.getFrameDuration());
			map.put(animation.getAction(), textureAnimation);
		}
	}

	public void put(ActionAnimation action, TextureAnimation animation) {
		map.put(action, animation);
	}

	public TextureAnimation get(ActionAnimation action) {
		return map.get(action);
	}
}