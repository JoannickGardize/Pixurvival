package com.pixurvival.gdxcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.util.MathUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MapActor extends Actor {

	private @NonNull TiledMap map;

	private float animationCounter;
	private long animationNumber;

	@Override
	public void draw(Batch batch, float parentAlpha) {
		animationCounter += Gdx.graphics.getDeltaTime();
		float tileAnimationSpeed = PixurvivalGame.getClient().getWorld().getContentPack().getConstants().getTileAnimationSpeed() / 1000f;
		if (animationCounter >= tileAnimationSpeed) {
			animationCounter -= tileAnimationSpeed;
			animationNumber++;
		}
		Vector3 camPos = getStage().getCamera().position;
		OrthographicCamera camera = (OrthographicCamera) getStage().getCamera();
		Viewport viewport = getStage().getViewport();
		float width = viewport.getWorldWidth() * camera.zoom;
		float height = viewport.getWorldHeight() * camera.zoom;
		int startX = MathUtils.floor(camPos.x - width / 2);
		int startY = MathUtils.floor(camPos.y - height / 2);
		int endX = (int) Math.ceil(startX + width);
		int endY = (int) Math.ceil(startY + height);
		for (int x = endX; x >= startX; x--) {
			for (int y = endY; y >= startY; y--) {
				batch.draw(PixurvivalGame.getContentPackTextures().getTile(map.tileAt(x, y).getTileDefinition().getId(), animationNumber), x - (float) GameConstants.PIXEL_SIZE,
						y - (float) GameConstants.PIXEL_SIZE, 1 + (float) GameConstants.PIXEL_SIZE, 1 + (float) GameConstants.PIXEL_SIZE);
			}
		}
	}
}
