package com.pixurvival.gdxcore.util;

import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.pixurvival.core.CustomDataHolder;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkPosition;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.drawer.DrawData;
import com.pixurvival.gdxcore.overlay.OverlayConstants;
import com.pixurvival.gdxcore.textures.ColorTextures;
import com.pixurvival.gdxcore.textures.TextureAnimation;
import com.pixurvival.gdxcore.textures.TextureAnimationSet;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DrawUtils {

	public static final float TOOLTIP_OFFSET = 10;

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
		timer += Gdx.graphics.getDeltaTime();
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
		tmpRectangle.set(rectangle.x, rectangle.y, rectangle.width, OverlayConstants.BAR_BORDER_SIZE);
		drawRectangle(batch, tmpRectangle, Color.BLACK);
		if (topBorder) {
			tmpRectangle.y = rectangle.y + rectangle.height - OverlayConstants.BAR_BORDER_SIZE;
			drawRectangle(batch, tmpRectangle, Color.BLACK);
		}
		tmpRectangle.set(rectangle.x, rectangle.y + OverlayConstants.BAR_BORDER_SIZE, OverlayConstants.BAR_BORDER_SIZE, rectangle.height - OverlayConstants.BAR_BORDER_SIZE * 2);
		drawRectangle(batch, tmpRectangle, Color.BLACK);
		tmpRectangle.x = rectangle.x + rectangle.width - OverlayConstants.BAR_BORDER_SIZE;
		drawRectangle(batch, tmpRectangle, Color.BLACK);
		tmpRectangle.set(rectangle.x + OverlayConstants.BAR_BORDER_SIZE, rectangle.y + OverlayConstants.BAR_BORDER_SIZE, (rectangle.width - OverlayConstants.BAR_BORDER_SIZE * 2) * percent,
				rectangle.height - OverlayConstants.BAR_BORDER_SIZE * 2);
		drawRectangle(batch, tmpRectangle, color);
		tmpRectangle.x += tmpRectangle.width;
		tmpRectangle.width = (rectangle.width - OverlayConstants.BAR_BORDER_SIZE * 2) * (1 - percent);
		drawRectangle(batch, tmpRectangle, Color.BLACK);
	}

	public static void drawRectangle(Batch batch, Rectangle rectangle, Color color) {
		batch.draw(ColorTextures.get(color), rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}

	public static void foreachChunksInScreen(Stage worldStage, double margin, Consumer<Chunk> action) {
		OrthographicCamera camera = (OrthographicCamera) worldStage.getCamera();
		Vector3 camPos = camera.position;
		float width = worldStage.getViewport().getWorldWidth() * camera.zoom;
		float height = worldStage.getViewport().getWorldHeight() * camera.zoom;
		int startX = MathUtils.floor((camPos.x - width / 2 - margin) / GameConstants.CHUNK_SIZE);
		int startY = MathUtils.floor((camPos.y - height / 2 - margin) / GameConstants.CHUNK_SIZE);
		int endX = MathUtils.ceil((camPos.x + width / 2 + margin) / GameConstants.CHUNK_SIZE);
		int endY = MathUtils.ceil((camPos.y + height / 2 + margin) / GameConstants.CHUNK_SIZE);
		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				Chunk chunk = PixurvivalGame.getWorld().getMap().chunkAt(new ChunkPosition(x, y));
				if (chunk == null) {
					continue;
				}
				action.accept(chunk);
			}
		}
	}

	public static void setTooltipPosition(Actor actor) {
		int x = Gdx.input.getX();
		int y = Gdx.graphics.getHeight() - Gdx.input.getY();
		if (x < Gdx.graphics.getWidth() / 2) {
			actor.setX(Math.min(x + TOOLTIP_OFFSET, Gdx.graphics.getWidth() - actor.getWidth()));
		} else {
			actor.setX(Math.max(x - TOOLTIP_OFFSET - actor.getWidth(), 0));
		}
		if (y < Gdx.graphics.getHeight() / 2) {
			actor.setY(Math.min(y + TOOLTIP_OFFSET, Gdx.graphics.getHeight() - actor.getHeight()));
		} else {
			actor.setY(Math.max(y - TOOLTIP_OFFSET - actor.getHeight(), 0));
		}
	}
}
