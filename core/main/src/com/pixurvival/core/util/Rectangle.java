package com.pixurvival.core.util;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Rectangle implements Serializable {

	private static final long serialVersionUID = 1L;

	private float startX;
	private float startY;
	private float endX;
	private float endY;

	public Rectangle(Vector2 center, float size) {
		startX = center.getX() - size / 2;
		startY = center.getY() - size / 2;
		endX = center.getX() + size / 2;
		endY = center.getY() + size / 2;
	}

	public boolean contains(Vector2 position) {
		return position.getX() >= startX && position.getX() <= endX && position.getY() >= startY && position.getY() <= endY;
	}

	public Vector2 getCenter() {
		return new Vector2((startX + endX) / 2, (startY + endY) / 2);
	}

	public float getWidth() {
		return endX - startX;
	}

	public float getHeight() {
		return endY - startY;
	}
}
