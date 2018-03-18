package com.pixurvival.gdxcore.ui;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.Entity;
import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.Position;
import com.pixurvival.core.map.TiledMapListener;
import com.pixurvival.gdxcore.PixurvivalGame;

public class MiniMapActor extends Actor implements TiledMapListener {

	private Map<Position, Texture> chunkTextures = new HashMap<>();
	private Texture background;
	private Texture mapElement;
	private long myPlayerId;
	private double worldViewSize = 200;
	private Color targetColor = new Color(0x4CA5FF);

	public MiniMapActor(long myPlayerId) {
		this.myPlayerId = myPlayerId;
		PixurvivalGame.getWorld().getMap().addListener(this);
		Pixmap pixmap = new Pixmap(1, 1, Format.RGB888);
		pixmap.drawPixel(0, 0, Color.rgb888(Color.BLACK));
		background = new Texture(pixmap);
		background.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		mapElement = new Texture(Gdx.files.internal("map_element.png"), true);
		mapElement.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(background, getX(), getY(), getWidth(), getHeight());
		Entity targetEntity = PixurvivalGame.getWorld().getEntityPool().get(EntityGroup.PLAYER, myPlayerId);
		if (targetEntity == null) {
			return;
		}
		double worldStartX = targetEntity.getPosition().x - worldViewSize / 2;
		double worldStartY = targetEntity.getPosition().y - worldViewSize / 2;
		double worldEndX = targetEntity.getPosition().x + worldViewSize / 2 + GameConstants.CHUNK_SIZE;
		double worldEndY = targetEntity.getPosition().y + worldViewSize / 2 + GameConstants.CHUNK_SIZE;
		float drawWidth = (float) (GameConstants.CHUNK_SIZE / worldViewSize * getWidth());
		float drawHeight = (float) (GameConstants.CHUNK_SIZE / worldViewSize * getHeight());
		for (double worldX = worldStartX; worldX < worldEndX; worldX += GameConstants.CHUNK_SIZE) {
			for (double worldY = worldStartY; worldY < worldEndY; worldY += GameConstants.CHUNK_SIZE) {
				Chunk chunk = PixurvivalGame.getWorld().getMap().chunkAt(worldX, worldY);
				if (chunk != null) {
					Texture texture = chunkTextures.get(chunk.getPosition());
					if (texture != null) {
						float drawX = (float) ((chunk.getOffsetX() - worldStartX) / worldViewSize * getWidth())
								+ getX();
						float drawY = (float) ((chunk.getOffsetY() - worldStartY) / worldViewSize * getHeight())
								+ getY();
						batch.draw(texture, drawX, drawY, drawWidth, drawHeight);
					}
				}
			}
		}

		batch.setColor(targetColor);
		batch.draw(mapElement, getX() + getWidth() / 2 - 5, getY() + getHeight() / 2 - 5, 10, 10);
		batch.setColor(Color.WHITE);
	}

	@Override
	public void chunkAdded(Chunk chunk) {
		Pixmap pixmap = new Pixmap(GameConstants.CHUNK_SIZE, GameConstants.CHUNK_SIZE, Format.RGBA8888);
		for (int x = 0; x < GameConstants.CHUNK_SIZE; x++) {
			for (int y = 0; y < GameConstants.CHUNK_SIZE; y++) {
				pixmap.drawPixel(x, y, PixurvivalGame.getContentPackTextures()
						.getTileColor(chunk.tileAtLocal(x, GameConstants.CHUNK_SIZE - 1 - y).getTileDefinition().getId()));
			}
		}
		Texture texture = new Texture(pixmap, true);
		texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		chunkTextures.put(chunk.getPosition().copy(), texture);
	}
}
