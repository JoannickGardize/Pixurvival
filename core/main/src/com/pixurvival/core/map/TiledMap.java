package com.pixurvival.core.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.core.Entity;
import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.message.MissingChunk;
import com.pixurvival.core.util.Vector2;

public class TiledMap {

	private Position tmpPosition = new Position();
	private Position tmpPosition2 = new Position();
	private List<MissingChunk> tmpMissingChunks = new ArrayList<>();
	private List<TiledMapListener> listeners = new ArrayList<>();

	private World world;

	private MapTile outsideTile;

	private Map<Position, Chunk> chunks = new HashMap<>();

	public TiledMap(World world) {
		this.world = world;
		world.getContentPack().getTilesById().forEach(t -> {
			if (t.getName().equals("deepWater")) {
				outsideTile = new EmptyTile(t);
			}
		});
	}

	public void addListener(TiledMapListener listener) {
		listeners.add(listener);
	}

	public MapTile tileAt(Vector2 position) {
		return tileAt((int) Math.floor(position.x), (int) Math.floor(position.y));
	}

	public MapTile tileAt(int x, int y) {
		Chunk chunk = chunkAt(x, y);
		if (chunk == null) {
			return outsideTile;
		} else {
			return chunk.tileAt(x, y);
		}
	}

	public void setChunk(CompressedChunk compressed) {
		Chunk chunk = compressed.buildChunk(world.getChunkSupplier().getMapTilesById(),
				world.getContentPack().getStructuresById());
		addChunk(chunk);
	}

	public void addChunk(Chunk chunk) {
		chunks.put(chunk.getPosition(), chunk);
		listeners.forEach(l -> l.chunkAdded(chunk));
	}

	public Chunk chunkAt(double x, double y) {
		tmpPosition.set((int) Math.floor(x / GameConstants.CHUNK_SIZE), (int) Math.floor(y / GameConstants.CHUNK_SIZE));
		return chunks.get(tmpPosition);
	}

	public Chunk chunkAt(Position position) {
		return chunks.get(position);
	}

	private void chunkPosition(Vector2 pos, Position position) {
		position.set((int) Math.floor(pos.getX() / GameConstants.CHUNK_SIZE), (int) Math.floor(pos.getY() / GameConstants.CHUNK_SIZE));
	}

	public void update() {
		world.getEntityPool().get(EntityGroup.PLAYER).forEach(p -> {
			PlayerEntity player = (PlayerEntity) p;
			chunkPosition(p.getPosition(), tmpPosition);
			boolean chunkChanged = false;
			if (player.getChunkPosition() == null) {
				chunkChanged = true;
				player.setChunkPosition(tmpPosition.copy());
			} else {
				chunkChanged = !player.getChunkPosition().equals(tmpPosition);
			}
			if (chunkChanged) {
				tmpMissingChunks.clear();
				for (int x = tmpPosition.getX() - GameConstants.PLAYER_CHUNK_VIEW_DISTANCE; x <= tmpPosition.getX()
						+ GameConstants.PLAYER_CHUNK_VIEW_DISTANCE; x++) {
					for (int y = tmpPosition.getY() - GameConstants.PLAYER_CHUNK_VIEW_DISTANCE; y <= tmpPosition.getY()
							+ GameConstants.PLAYER_CHUNK_VIEW_DISTANCE; y++) {
						tmpPosition2.set(x, y);
						if (chunks.get(tmpPosition2) == null) {
							if (world.isServer()) {
								Chunk chunk = world.getChunkSupplier().get(x, y);
								addChunk(chunk);
							} else {
								tmpMissingChunks.add(new MissingChunk(tmpPosition2.getX(), tmpPosition2.getY()));
							}
						}
					}
				}
			}
		});
	}

	public MissingChunk[] pollMissingChunks() {
		if (tmpMissingChunks.isEmpty()) {
			return null;
		} else {
			MissingChunk[] result = tmpMissingChunks.toArray(new MissingChunk[tmpMissingChunks.size()]);
			tmpMissingChunks.clear();
			return result;
		}
	}

	public boolean collide(Entity e) {
		return collide(e.getPosition().x, e.getPosition().y, e.getBoundingRadius());
	}

	public boolean collide(Entity e, double dx, double dy) {
		return collide(e.getPosition().x + dx, e.getPosition().y + dy, e.getBoundingRadius());
	}

	public boolean collide(double x, double y, double radius) {
		int tileX = (int) Math.floor(x - radius);
		int startY = (int) Math.floor(y - radius);
		double right = x + radius;
		int endX = (int) Math.floor(right);
		if (right == endX) {
			endX--;
		}
		double top = y + radius;
		int endY = (int) Math.floor(top);
		if (endY == top) {
			endY--;
		}
		for (; tileX <= endX; tileX++) {
			for (int tileY = startY; tileY <= endY; tileY++) {
				if (tileAt(tileX, tileY).isSolid()) {
					return true;
				}
			}
		}
		return false;
	}

}
