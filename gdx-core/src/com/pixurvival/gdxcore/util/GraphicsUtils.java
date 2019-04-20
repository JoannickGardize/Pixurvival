package com.pixurvival.gdxcore.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.CustomDataHolder;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.gdxcore.drawer.DrawData;
import com.pixurvival.gdxcore.textures.TextureAnimation;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GraphicsUtils {

	public static int getIndexAndUpdateTimer(CustomDataHolder e, TextureAnimation textureAnimation) {
		DrawData o = (DrawData) e.getCustomData();
		if (o == null) {
			o = new DrawData();
			e.setCustomData(o);
			if (e instanceof Entity) {
				o.getDrawPosition().set(((Entity) e).getPosition());
			}
		}
		DrawData data = o;
		float timer = data.getTimer();
		timer += Gdx.graphics.getRawDeltaTime();
		while (timer >= textureAnimation.getFrameDuration() * textureAnimation.size()) {
			timer -= textureAnimation.getFrameDuration() * textureAnimation.size();
		}
		data.setTimer(timer);
		return (int) (timer / textureAnimation.getFrameDuration());
	}

	public static void drawStandUpStyleTexture(Batch batch, TextureAnimationSet textureAnimationSet, ActionAnimation actionAnimation, int index, Vector2 position, float yOffset) {
		TextureAnimation textureAnimation = textureAnimationSet.get(actionAnimation);
		Texture texture = textureAnimation.getTexture(index);
		float x = (float) (position.getX() - textureAnimationSet.getWidth() / 2);
		float y = (float) position.getY() + textureAnimationSet.getYOffset();
		batch.draw(texture, x, y, textureAnimationSet.getWidth(), textureAnimationSet.getHeight() + yOffset, 0, 1 + yOffset, 1, 0);
	}

	public static void drawRotatedStandUpStyleTexture(Batch batch, TextureAnimationSet textureAnimationSet, ActionAnimation actionAnimation, int index, Vector2 position, float rotation) {
		TextureAnimation textureAnimation = textureAnimationSet.get(actionAnimation);
		Texture texture = textureAnimation.getTexture(index);
		float x = (float) (position.getX() - textureAnimationSet.getWidth() / 2);
		float y = (float) position.getY() + textureAnimationSet.getYOffset();
		batch.draw(texture, x, y, textureAnimationSet.getWidth() / 2f, textureAnimationSet.getHeight() / 2f, textureAnimationSet.getWidth(), textureAnimationSet.getHeight(), 1, 1, rotation, 0, 0,
				texture.getWidth(), texture.getHeight(), false, false);
	}
}
