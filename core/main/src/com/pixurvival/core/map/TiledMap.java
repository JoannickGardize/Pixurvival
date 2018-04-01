package com.pixurvival.core.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.pixurvival.core.Entity;
import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.message.HarvestableStructureUpdate;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;

public class TiledMap {

	private List<Position> tmpMissingChunks = new ArrayList<>();
	private List<TiledMapListener> listeners = new ArrayList<>();
	private List<Chunk> newChunks = new ArrayList<>();

	@Getter
	private World world;

	private MapTile outsideTile;
	private int chunkDistance;

	private Map<Position, Chunk> chunks = new HashMap<>();

	public TiledMap(World world) {
		this.world = world;
		world.getContentPack().getTilesById().forEach(t -> {
			if (t.getName().equals("deepWater")) {
				outsideTile = new EmptyTile(t);
			}
		});
		chunkDistance = world.isServer() ? GameConstants.KEEP_ALIVE_CHUNK_VIEW_DISTANCE
				: GameConstants.PLAYER_CHUNK_VIEW_DISTANCE;
	}

	public void addListener(TiledMapListener listener) {
		listeners.add(listener);
	}

	public void notifyListeners(Consumer<TiledMapListener> action) {
		listeners.forEach(action);
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

	public void addChunk(Chunk chunk) {
		newChunks.add(chunk);
	}

	public void setChunk(CompressedChunk compressed) {
		Chunk chunk = compressed.buildChunk(world.getChunkSupplier().getMapTilesById(),
				world.getContentPack().getStructuresById());
		insertChunk(chunk);
	}

	private void insertChunk(Chunk chunk) {
		chunks.put(chunk.getPosition(), chunk);
		listeners.forEach(l -> l.chunkAdded(chunk));
	}

	public Chunk chunkAt(double x, double y) {
		Position position = new Position((int) Math.floor(x / GameConstants.CHUNK_SIZE),
				(int) Math.floor(y / GameConstants.CHUNK_SIZE));
		return chunks.get(position);
	}

	public Chunk chunkAt(Position position) {
		return chunks.get(position);
	}

	private Position chunkPosition(Vector2 pos) {
		return new Position((int) Math.floor(pos.getX() / GameConstants.CHUNK_SIZE),
				(int) Math.floor(pos.getY() / GameConstants.CHUNK_SIZE));
	}

	public void update() {
		synchronized (this) {
			newChunks.forEach(c -> insertChunk(c));
			newChunks.clear();
		}
		world.getEntityPool().get(EntityGroup.PLAYER).forEach(p -> {
			PlayerEntity player = (PlayerEntity) p;
			Position chunkPosition = chunkPosition(p.getPosition());
			boolean chunkChanged = false;
			if (player.getChunkPosition() == null) {
				chunkChanged = true;
				player.setChunkPosition(chunkPosition);
			} else {
				chunkChanged = !player.getChunkPosition().equals(chunkPosition);
			}
			if (chunkChanged) {
				player.setChunkPosition(chunkPosition);
				tmpMissingChunks.clear();
				for (int x = chunkPosition.getX() - chunkDistance; x <= chunkPosition.getX() + chunkDistance; x++) {
					for (int y = chunkPosition.getY() - chunkDistance; y <= chunkPosition.getY() + chunkDistance; y++) {
						Position position = new Position(x, y);
						if (!chunks.containsKey(position)) {
							chunks.put(position, null);
							if (world.isServer()) {
								ChunkManager.getInstance().requestChunk(this, position);
							} else {
								tmpMissingChunks.add(position);
							}
						}
					}
				}
			}
		});
	}

	public void applyUpdate(HarvestableStructureUpdate[] structureUpdates) {
		if (structureUpdates == null) {
			return;
		}
		for (HarvestableStructureUpdate structureUpdate : structureUpdates) {
			MapTile mapTile = tileAt(structureUpdate.getX(), structureUpdate.getY());
			if (mapTile instanceof TileAndStructure) {
				((HarvestableStructure) mapTile.getStructure()).setHarvested(structureUpdate.isHarvested());
			}
		}
	}

	public Position[] pollMissingChunks() {
		if (tmpMissingChunks.isEmpty()) {
			return null;
		} else {
			Position[] result = tmpMissingChunks.toArray(new Position[tmpMissingChunks.size()]);
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
