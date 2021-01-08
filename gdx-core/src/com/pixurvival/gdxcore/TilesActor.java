package com.pixurvival.gdxcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.map.chunk.ChunkPosition;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.gdxcore.textures.ChunkTileTextures;
import com.pixurvival.gdxcore.textures.ChunkTileTexturesManager;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TilesActor extends Actor {

	private @NonNull TiledMap map;

	private float animationCounter;
	private long animationNumber;

	@Override
	public void draw(Batch batch, float parentAlpha) {
		animationCounter += Gdx.graphics.getRawDeltaTime();
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
		if (startX == endX || startY == endY) {
			return;
		}
		ChunkTileTexturesManager chunkTileTexturesManager = PixurvivalGame.getChunkTileTexturesManager();
		int actualEndX;
		int actualEndY = startY;
		while (startY <= endY) {
			int actualStartX = startX;
			while (actualStartX <= endX) {
				ChunkPosition chunkPosition = ChunkPosition.fromWorldPosition(actualStartX, startY);
				ChunkTileTextures chunkTileTextures = chunkTileTexturesManager.get(chunkPosition);
				int chunkXLimit = chunkPosition.getX() * GameConstants.CHUNK_SIZE + GameConstants.CHUNK_SIZE - 1;
				int chunkYLimit = chunkPosition.getY() * GameConstants.CHUNK_SIZE + GameConstants.CHUNK_SIZE - 1;
				actualEndX = endX > chunkXLimit ? chunkXLimit : endX;
				actualEndY = endY > chunkYLimit ? chunkYLimit : endY;
				if (chunkTileTextures != null) {
					for (int x = actualStartX; x <= actualEndX; x++) {
						for (int y = startY; y <= actualEndY; y++) {
							Texture[] textures = chunkTileTextures.getTexturesAt(x, y);
							batch.draw(textures[(int) (animationNumber % textures.length)], x - GameConstants.PIXEL_SIZE, y - GameConstants.PIXEL_SIZE, 1 + GameConstants.PIXEL_SIZE,
									1 + GameConstants.PIXEL_SIZE);
						}
					}
				}
				actualStartX = actualEndX + 1;
			}
			startY = actualEndY + 1;
		}
	}
}
