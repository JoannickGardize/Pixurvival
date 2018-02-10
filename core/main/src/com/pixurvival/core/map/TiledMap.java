package com.pixurvival.core.map;

import com.pixurvival.core.Entity;
import com.pixurvival.core.util.Array2D;

import lombok.Getter;

@Getter
public class TiledMap {

	Array2D<Tile> tiles;

	public TiledMap(int width, int height) {
		tiles = new Array2D<>(width, height);
	}

	public Tile tileAt(int x, int y) {
		return tiles.get(x, y);
	}

	public void setTileAt(int x, int y, Tile tile) {
		tiles.set(x, y, tile);
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
				if (tileX < 0 || tileY < 0 || tileX >= tiles.getWidth() || tileY >= tiles.getHeight()
						|| tiles.get(tileX, tileY).isSolid()) {
					return true;
				}
			}
		}
		return false;
	}
}
