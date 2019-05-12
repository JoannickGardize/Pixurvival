package com.pixurvival.core.contentPack;

import java.io.Serializable;

import com.pixurvival.core.util.Vector2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rectangle implements Serializable {

	private static final long serialVersionUID = 1L;

	private double x;
	private double y;
	private double width;
	private double height;

	public boolean contains(Vector2 position) {
		return position.getX() >= x && position.getX() <= x + width && position.getY() >= y && position.getY() <= y + height;
	}
}
