package com.pixurvival.gdxcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
		animationCounter += Gdx.graphics.getRawDeltaTime() * 5;
		if (animationCounter >= 1) {
			animationCounter -= 1;
			animationNumber++;
		}
		Vector3 camPos = getStage().getCamera().position;
		OrthographicCamera camera = (OrthographicCamera) getStage().getCamera();
		float width = getStage().getViewport().getWorldWidth() * camera.zoom;
		float height = getStage().getViewport().getWorldHeight() * camera.zoom;
		int startX = MathUtils.floor(camPos.x - width / 2);
		int startY = MathUtils.floor(camPos.y - height / 2);
		int endX = (int) Math.ceil(startX + width);
		int endY = (int) Math.ceil(startY + height);
		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				batch.draw(PixurvivalGame.getContentPackTextures().getTile(map.tileAt(x, y).getTileDefinition().getId(), animationNumber), x, y, 1, 1);
			}
		}
	}
}
