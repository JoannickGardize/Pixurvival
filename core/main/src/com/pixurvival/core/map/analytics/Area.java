package com.pixurvival.core.map.analytics;

import com.pixurvival.core.util.Vector2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Area {

	private int startX;
	private int startY;
	private int endX;
	private int endY;

	public Area(Vector2 position) {
		this((int) Math.round(position.getX()), (int) Math.round(position.getY()));
	}

	public Area(Position initialPosition) {
		this(initialPosition.getX(), initialPosition.getY());
	}

	public Area(int x, int y) {
		startX = endX = x;
		startY = endY = y;
	}

	public boolean contains(Position position) {
		return position.getX() >= startX && position.getX() <= endX && position.getY() >= startY && position.getY() <= endY;
	}

	public void enclose(Position position) {
		if (position.getX() < startX) {
			startX = position.getX();
		} else if (position.getX() > endX) {
			endX = position.getX();
		}
		if (position.getY() < startY) {
			startY = position.getY();
		} else if (position.getY() > endY) {
			endY = position.getY();
		}
	}

	public int enclosingWidth(Position position) {
		return Math.max(endX, position.getX()) - Math.min(startX, position.getX());
	}

	public int enclosingHeight(Position position) {
		return Math.max(endY, position.getY()) - Math.min(startY, position.getY());
	}

	public int width() {
		return endX - startX;
	}

	public int height() {
		return endY - startY;
	}

	public Vector2 center() {
		return new Vector2((startX + endX) / 2.0, (startY + endY) / 2.0);
	}
}
