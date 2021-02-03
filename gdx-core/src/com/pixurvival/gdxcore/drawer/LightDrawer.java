package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.pixurvival.core.map.Light;
import com.pixurvival.core.time.DayCycleRun;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.ContentPackAssets;
import com.pixurvival.gdxcore.util.DrawUtils;

public class LightDrawer {
	public static final Color NIGHT_COLOR = new Color(0.4f, 0.4f, 0.4f, 1f);
	public static final float START_FADE_OUT = 0.01f;
	public static final float START_FADE_IN = 0.95f;

	private Color tmpColor = new Color(NIGHT_COLOR);
	private FrameBuffer lightBuffer;
	private TextureRegion lightBufferRegion;

	public void draw(Stage worldStage) {
		Color ambientColor = getAmbientColor();
		if (ambientColor == null) {
			return;
		}

		lightBuffer.begin();
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		// ambient color
		Gdx.gl.glClearColor(ambientColor.r, ambientColor.g, ambientColor.b, ambientColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Batch batch = worldStage.getBatch();
		Camera camera = worldStage.getCamera();
		batch.begin();
		// light color
		batch.setColor(1f, 1f, 1f, 1f);
		ContentPackAssets cpt = PixurvivalGame.getContentPackTextures();
		DrawUtils.foreachChunksInScreen(worldStage, cpt.getLargestLightRadius(), chunk -> {
			for (Light light : chunk.getLights()) {
				Texture texture = cpt.getLightTexture(light.getRadius());
				batch.draw(texture, light.getPosition().getX() - light.getRadius(), light.getPosition().getY() - light.getRadius(), light.getRadius() * 2, light.getRadius() * 2);
			}
		});
		batch.end();
		lightBuffer.end();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_SRC_COLOR);
		batch.enableBlending();
		batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR);
		batch.begin();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_SRC_COLOR);
		worldStage.getViewport().apply();
		batch.draw(lightBufferRegion, camera.position.x - worldStage.getWidth() / 2, camera.position.y - worldStage.getHeight() / 2, worldStage.getWidth(), worldStage.getHeight());
		batch.end();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}

	public void resize(int screenWidth, int screenHeight) {
		int size = Math.min(screenWidth, screenHeight);
		dispose();
		lightBuffer = new FrameBuffer(Format.RGBA8888, size, size, false);
		lightBuffer.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		lightBufferRegion = new TextureRegion(lightBuffer.getColorBufferTexture(), 0, 0, lightBuffer.getWidth(), lightBuffer.getHeight());
		lightBufferRegion.flip(false, true);
	}

	public void dispose() {
		if (lightBuffer != null) {
			lightBuffer.dispose();
		}
	}

	private Color getAmbientColor() {
		DayCycleRun dayCycle = PixurvivalGame.getWorld().getTime().getDayCycle();
		if (dayCycle.isDay()) {
			if (dayCycle.currentMomentProgress() > START_FADE_OUT && dayCycle.currentMomentProgress() < START_FADE_IN) {
				return null;
			} else {
				float alpha = getAlpha(dayCycle);
				tmpColor.r = 1 - (1 - NIGHT_COLOR.r) * alpha;
				tmpColor.g = 1 - (1 - NIGHT_COLOR.g) * alpha;
				tmpColor.b = 1 - (1 - NIGHT_COLOR.b) * alpha;
			}
		} else {
			tmpColor.r = NIGHT_COLOR.r;
			tmpColor.g = NIGHT_COLOR.g;
			tmpColor.b = NIGHT_COLOR.b;
		}
		return tmpColor;
	}

	private float getAlpha(DayCycleRun dayCycle) {
		if (dayCycle.currentMomentProgress() < START_FADE_OUT) {
			return 1f - dayCycle.currentMomentProgress() / START_FADE_OUT;
		} else if (dayCycle.currentMomentProgress() > START_FADE_IN) {
			return (dayCycle.currentMomentProgress() - START_FADE_IN) / (1 - START_FADE_IN);
		} else {
			return 0;
		}
	}
}
