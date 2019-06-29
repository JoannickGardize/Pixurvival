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
import com.pixurvival.gdxcore.textures.ContentPackTextures;
import com.pixurvival.gdxcore.util.DrawUtils;

public class LightDrawer {
	public static final Color NIGHT_COLOR = new Color(0.4f, 0.4f, 0.4f, 1f);
	public static final double START_FADE = 0.95;

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
		// batch.draw(ColorTextures.get(Color.WHITE), camera.position.x -
		// lightBufferRegion.getRegionWidth() / 2f + 1, camera.position.y -
		// lightBufferRegion.getRegionHeight() / 2f + 2,
		// lightBufferRegion.getRegionWidth() - 2,
		// lightBufferRegion.getRegionHeight() - 2);
		ContentPackTextures cpt = PixurvivalGame.getContentPackTextures();
		DrawUtils.foreachChunksInScreen(worldStage, cpt.getLargestLightRadius(), chunk -> {
			for (Light light : chunk.getLights()) {
				Texture texture = cpt.getLightTexture(light.getRadius());
				batch.draw(texture, (float) (light.getPosition().getX() - light.getRadius()), (float) (light.getPosition().getY() - light.getRadius()), (float) (light.getRadius() * 2),
						(float) (light.getRadius() * 2));
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
		lightBuffer = new FrameBuffer(Format.RGBA8888, size, size, false);
		lightBuffer.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		lightBufferRegion = new TextureRegion(lightBuffer.getColorBufferTexture(), 0, 0, lightBuffer.getWidth(), lightBuffer.getHeight());
		lightBufferRegion.flip(false, true);
	}

	private Color getAmbientColor() {
		DayCycleRun dayCycle = PixurvivalGame.getWorld().getTime().getDayCycle();
		if (dayCycle.isDay()) {
			if (dayCycle.currentMomentProgress() < START_FADE) {
				return null;
			} else {
				float alpha = getAlpha(dayCycle);
				tmpColor.r = 1 - (1 - NIGHT_COLOR.r) * alpha;
				tmpColor.g = 1 - (1 - NIGHT_COLOR.g) * alpha;
				tmpColor.b = 1 - (1 - NIGHT_COLOR.b) * alpha;
			}
		} else {
			float alpha = 1 - getAlpha(dayCycle);
			tmpColor.r = 1 - (1 - NIGHT_COLOR.r) * alpha;
			tmpColor.g = 1 - (1 - NIGHT_COLOR.g) * alpha;
			tmpColor.b = 1 - (1 - NIGHT_COLOR.b) * alpha;
		}
		return tmpColor;
	}

	private float getAlpha(DayCycleRun dayCycle) {
		if (dayCycle.currentMomentProgress() >= START_FADE) {
			return (float) ((dayCycle.currentMomentProgress() - START_FADE) / (1 - START_FADE));
		} else {
			return 0;
		}
	}
}
