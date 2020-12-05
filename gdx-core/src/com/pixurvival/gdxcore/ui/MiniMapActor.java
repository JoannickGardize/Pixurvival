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
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.PlayerMapEventListener;
import com.pixurvival.core.map.TiledMapListener;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkPosition;
import com.pixurvival.core.map.chunk.update.StructureUpdate;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.ColorTextures;

public class MiniMapActor extends Actor implements TiledMapListener, PlayerMapEventListener {

	private Map<ChunkPosition, Texture> chunkTextures = new HashMap<>();
	private Texture background;
	private Texture mapElement;
	private float worldViewSize = 200;
	private Color targetColor = new Color(0x4CA5FF);

	public MiniMapActor() {
		PixurvivalGame.getWorld().getMap().addListener(this);
		PixurvivalGame.getWorld().getMap().addPlayerMapEventListener(this);
		background = ColorTextures.get(Color.BLACK);
		mapElement = new Texture(Gdx.files.internal("map_element.png"), true);
		mapElement.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(background, getX(), getY(), getWidth(), getHeight());
		float worldViewSizeX = getWidth() > getHeight() ? worldViewSize * getWidth() / getHeight() : worldViewSize;
		float worldViewSizeY = getWidth() > getHeight() ? worldViewSize : worldViewSize * getHeight() / getWidth();
		Entity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		float worldStartX = myPlayer.getPosition().getX() - worldViewSizeX / 2;
		float worldStartY = myPlayer.getPosition().getY() - worldViewSizeY / 2;
		float worldEndX = myPlayer.getPosition().getX() + worldViewSizeX / 2 + GameConstants.CHUNK_SIZE;
		float worldEndY = myPlayer.getPosition().getY() + worldViewSizeY / 2 + GameConstants.CHUNK_SIZE;
		float minSize = Math.min(getWidth(), getHeight());
		float drawWidth = GameConstants.CHUNK_SIZE / worldViewSize * minSize;
		float drawHeight = GameConstants.CHUNK_SIZE / worldViewSize * minSize;
		for (float worldX = worldStartX; worldX < worldEndX; worldX += GameConstants.CHUNK_SIZE) {
			for (float worldY = worldStartY; worldY < worldEndY; worldY += GameConstants.CHUNK_SIZE) {
				ChunkPosition position = new ChunkPosition(MathUtils.floor(worldX / GameConstants.CHUNK_SIZE), MathUtils.floor(worldY / GameConstants.CHUNK_SIZE));
				Texture texture = chunkTextures.get(position);
				if (texture != null) {
					float drawX = (position.getX() * GameConstants.CHUNK_SIZE - worldStartX) / worldViewSize * minSize + getX();
					float drawY = (position.getY() * GameConstants.CHUNK_SIZE - worldStartY) / worldViewSize * minSize + getY();
					batch.draw(texture, drawX, drawY, drawWidth, drawHeight);
				}
			}
		}

		batch.setColor(targetColor);
		batch.draw(mapElement, getX() + getWidth() / 2 - 5, getY() + getHeight() / 2 - 5, 10, 10);
		batch.setColor(Color.WHITE);
	}

	@Override
	public void chunkLoaded(Chunk chunk) {
		if (chunk.getPosition().insideSquare(PixurvivalGame.getClient().getMyPlayer().getPosition(), GameConstants.PLAYER_VIEW_DISTANCE)) {
			addChunk(chunk);
		}
	}

	@Override
	public void chunkUnloaded(Chunk chunk) {

	}

	@Override
	public void structureChanged(MapStructure mapStructure, StructureUpdate structureUpdate) {
	}

	@Override
	public void structureAdded(MapStructure mapStructure) {
	}

	@Override
	public void structureRemoved(MapStructure mapStructure) {
	}

	@Override
	public void enterVision(PlayerEntity entity, ChunkPosition position) {
		if (entity == PixurvivalGame.getClient().getMyPlayer()) {
			Chunk chunk = PixurvivalGame.getWorld().getMap().chunkAt(position);
			if (chunk != null) {
				addChunk(chunk);
			}
		}
	}

	@Override
	public void exitVision(PlayerEntity entity, ChunkPosition position) {
	}

	private void addChunk(Chunk chunk) {
		if (chunkTextures.containsKey(chunk.getPosition())) {
			return;
		}
		Pixmap pixmap = new Pixmap(GameConstants.CHUNK_SIZE, GameConstants.CHUNK_SIZE, Format.RGBA8888);
		for (int x = 0; x < GameConstants.CHUNK_SIZE; x++) {
			for (int y = 0; y < GameConstants.CHUNK_SIZE; y++) {
				pixmap.drawPixel(x, y, PixurvivalGame.getContentPackTextures().getTileColor(chunk.tileAtLocal(x, GameConstants.CHUNK_SIZE - 1 - y).getTileDefinition().getId()));
			}
		}
		Texture texture = new Texture(pixmap, true);
		texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		chunkTextures.put(chunk.getPosition(), texture);
	}

	@Override
	public void entityEnterChunk(ChunkPosition previousPosition, Entity e) {
	}

}
