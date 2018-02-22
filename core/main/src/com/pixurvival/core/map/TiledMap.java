package com.pixurvival.core.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.core.Entity;
import com.pixurvival.core.contentPack.Tile;
import com.pixurvival.core.util.ByteArray2D;

public class TiledMap {

	private ByteArray2D map;
	private List<MapTile> tiles = new ArrayList<>();
	private Map<TilePosition, MapTile> specialTiles = new HashMap<>();

	public TiledMap(List<Tile> tileTypes, int width, int height) {
		tileTypes.forEach(t -> tiles.add(new EmptyTile(t)));
		map = new ByteArray2D(width, height);
	}

	public MapTile tileAt(int x, int y) {
		byte id = map.get(x, y);
		if (id == Tile.SPECIAL_TILE) {
			return specialTiles.get(new TilePosition(x, y));
		} else {
			return tiles.get(id);
		}
	}

	public void setTileAt(int x, int y, Tile tile) {
		map.set(x, y, tile.getId());
	}

	public void setAll(Tile tile) {
		map.fill(tile.getId());
	}

	public boolean collide(Entity e) {
		double radius = e.getBoundingRadius();
		return collide(e.getPosition().x - radius, e.getPosition().y - radius, radius * 2);
	}

	public boolean collide(Entity e, double dx, double dy) {
		double radius = e.getBoundingRadius();
		return collide(e.getPosition().x - radius + dx, e.getPosition().y - radius + dy, radius * 2);
	}

	public boolean collide(double x, double y, double width) {
		int tileX = (int) x;
		if (tileX == x) {
			tileX++;
		}
		int startY = (int) y;
		if (startY == y) {
			startY++;
		}
		for (; tileX < x + width; tileX++) {
			for (int tileY = startY; tileY < y + width; tileY++) {
				if (tileX < 0 || tileY < 0 || tileX >= map.getWidth() || tileY >= map.getHeight()
						|| tileAt(tileX, tileY).isSolid()) {
					return true;
				}
			}
		}
		return false;
	}
}
