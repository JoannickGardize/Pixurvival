package com.pixurvival.gdxcore.graphics;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import com.badlogic.gdx.graphics.Pixmap;
import com.pixurvival.core.contentPack.ActionAnimation;
import com.pixurvival.core.contentPack.Animation;
import com.pixurvival.core.contentPack.AnimationTemplate;
import com.pixurvival.core.contentPack.ContentPackReadException;
import com.pixurvival.core.contentPack.SpriteSheet;
import com.pixurvival.gdxcore.graphics.SpriteSheetPixmap.Region;

public class TextureAnimationSet {

	private Map<ActionAnimation, TextureAnimation> map = new HashMap<>();

	public TextureAnimationSet(SpriteSheet spriteSheet, Function<Region, Pixmap> transform)
			throws ContentPackReadException {
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