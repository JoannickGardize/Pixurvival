package com.pixurvival.gdxcore.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.pixurvival.core.CustomDataHolder;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.gdxcore.drawer.DrawData;
import com.pixurvival.gdxcore.overlay.OverlaySettings;
import com.pixurvival.gdxcore.textures.ColorTextures;
import com.pixurvival.gdxcore.textures.TextureAnimation;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DrawUtils {

	private static final Rectangle tmpRectangle = new Rectangle();

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
			data.setFirstLoop(false);
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

	public static void drawPercentBar(Batch batch, Rectangle rectangle, float percent, Color color) {
		drawPercentBar(batch, rectangle, percent, color, true);
	}

	public static void drawPercentBar(Batch batch, Rectangle rectangle, float percent, Color color, boolean topBorder) {
		tmpRectangle.set(rectangle.x, rectangle.y, rectangle.width, OverlaySettings.BAR_BORDER_SIZE);
		drawRectangle(batch, tmpRectangle, Color.BLACK);
		if (topBorder) {
			tmpRectangle.y = rectangle.y + rectangle.height - OverlaySettings.BAR_BORDER_SIZE;
			drawRectangle(batch, tmpRectangle, Color.BLACK);
		}
		tmpRectangle.set(rectangle.x, rectangle.y + OverlaySettings.BAR_BORDER_SIZE, OverlaySettings.BAR_BORDER_SIZE, rectangle.height - OverlaySettings.BAR_BORDER_SIZE * 2);
		drawRectangle(batch, tmpRectangle, Color.BLACK);
		tmpRectangle.x = rectangle.x + rectangle.width - OverlaySettings.BAR_BORDER_SIZE;
		drawRectangle(batch, tmpRectangle, Color.BLACK);
		tmpRectangle.set(rectangle.x + OverlaySettings.BAR_BORDER_SIZE, rectangle.y + OverlaySettings.BAR_BORDER_SIZE, (rectangle.width - OverlaySettings.BAR_BORDER_SIZE * 2) * percent,
				rectangle.height - OverlaySettings.BAR_BORDER_SIZE * 2);
		drawRectangle(batch, tmpRectangle, color);
		tmpRectangle.x += tmpRectangle.width;
		tmpRectangle.width = (rectangle.width - OverlaySettings.BAR_BORDER_SIZE * 2) * (1 - percent);
		drawRectangle(batch, tmpRectangle, Color.BLACK);
	}

	public static void drawRectangle(Batch batch, Rectangle rectangle, Color color) {
		batch.draw(ColorTextures.get(color), rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}
}
