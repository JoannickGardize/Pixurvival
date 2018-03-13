package com.pixurvival.core.map;

import java.util.HashMap;
import java.util.Map;

import com.pixurvival.core.Entity;
import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.World;
import com.pixurvival.core.util.Vector2;

public class TiledMap {

	private static ThreadLocal<Position> tmpPosition = ThreadLocal.withInitial(() -> new Position());

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

	public MapTile tileAt(Vector2 position) {
		return tileAt((int) Math.floor(position.x), (int) Math.floor(position.y));
	}

	public MapTile tileAt(int x, int y) {
		Position position = tmpPosition.get();
		position.set((int) Math.floor((double) x / Chunk.CHUNK_SIZE), (int) Math.floor((double) y / Chunk.CHUNK_SIZE));
		Chunk chunk = chunks.get(position);
		if (chunk == null) {
			return outsideTile;
		} else {
			return chunk.tileAt(x, y);
		}
	}

	private Position chunkPosition(Vector2 pos) {
		return new Position((int) Math.floor(pos.getX() / Chunk.CHUNK_SIZE),
				(int) Math.floor(pos.getY() / Chunk.CHUNK_SIZE));
	}

	public void update() {
		Position position = tmpPosition.get();
		world.getEntityPool().get(EntityGroup.PLAYER).forEach(p -> {
			Position chunkPosition = chunkPosition(p.getPosition());
			for (int x = chunkPosition.getX() - World.PLAYER_CHUNK_VIEW_DISTANCE; x <= chunkPosition.getX()
					+ World.PLAYER_CHUNK_VIEW_DISTANCE; x++) {
				for (int y = chunkPosition.getY() - World.PLAYER_CHUNK_VIEW_DISTANCE; y <= chunkPosition.getY()
						+ World.PLAYER_CHUNK_VIEW_DISTANCE; y++) {
					position.set(x, y);
					if (chunks.get(position) == null) {
						Chunk chunk = world.getChunkSupplier().get(x, y);
						chunks.put(chunk.getPosition(), chunk);
					}
				}
			}
		});
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
