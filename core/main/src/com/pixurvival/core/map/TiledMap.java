package com.pixurvival.core.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.core.Entity;
import com.pixurvival.core.contentPack.Tile;
import com.pixurvival.core.util.ByteArray2D;

import lombok.Getter;

public class TiledMap {

	private @Getter ByteArray2D data;
	private List<MapTile> tiles = new ArrayList<>();
	private MapTile outsideTile;
	private Map<TilePosition, MapTile> specialTiles = new HashMap<>();

	public TiledMap(List<Tile> tileTypes, int width, int height) {
		tileTypes.forEach(t -> {
			EmptyTile et = new EmptyTile(t);
			tiles.add(et);
			if (t.getName().equals("deepWater")) {
				outsideTile = et;
			}
		});
		data = new ByteArray2D(width, height);
	}

	public TiledMap(List<Tile> tileTypes, ByteArray2D buildingMap) {
		tileTypes.forEach(t -> tiles.add(new EmptyTile(t)));
		data = buildingMap;
	}

	public MapTile tileAt(int x, int y) {
		if (x < 0 || y < 0 || x >= data.getWidth() || y >= data.getHeight()) {
			return outsideTile;
		}
		byte id = data.get(x, y);
		if (id == Tile.SPECIAL_TILE) {
			return specialTiles.get(new TilePosition(x, y));
		} else {
			return tiles.get(id);
		}
	}

	public void setTileAt(int x, int y, Tile tile) {
		data.set(x, y, tile.getId());
	}

	public void setAll(Tile tile) {
		data.fill(tile.getId());
	}

	public boolean collide(Entity e) {
		return collide(e.getPosition().x, e.getPosition().y, e.getBoundingRadius());
	}

	public boolean collide(Entity e, double dx, double dy) {
		return collide(e.getPosition().x + dx, e.getPosition().y + dy, e.getBoundingRadius());
	}

	public boolean collide(double x, double y, double radius) {

		for (int tileX = (int) (x - radius); tileX < x + radius; tileX++) {
			for (int tileY = (int) (y - radius); tileY < y + radius; tileY++) {
				if (tileAt(tileX, tileY).isSolid()) {
					return true;
				}
			}
		}
		return false;
	}
}
