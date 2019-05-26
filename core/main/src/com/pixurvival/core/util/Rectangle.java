package com.pixurvival.core.util;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Rectangle implements Serializable {

	private static final long serialVersionUID = 1L;

	private double startX;
	private double startY;
	private double endX;
	private double endY;

	public Rectangle(Vector2 center, double size) {
		startX = center.getX() - size / 2;
		startY = center.getY() - size / 2;
		endX = center.getX() + size / 2;
		endY = center.getY() + size / 2;
	}

	public boolean contains(Vector2 position) {
		return position.getX() >= startX && position.getX() <= endX && position.getY() >= startY && position.getY() <= endY;
	}
}
