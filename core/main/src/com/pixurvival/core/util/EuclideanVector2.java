package com.pixurvival.core.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(of = { "length", "direction" })
@ToString(of = { "length", "direction" })
@Getter
@NoArgsConstructor
public class EuclideanVector2 implements BaseVector2 {

	private double length;
	private double direction;
	private double x;
	private double y;

	public EuclideanVector2(double length, double direction) {
		this.length = length;
		this.direction = MathUtils.normalizeAngle(direction);
		computeCoordinates();
	}

	public void set(double length, double direction) {
		this.length = length;
		this.direction = MathUtils.normalizeAngle(direction);
		computeCoordinates();
	}

	public void setLength(double length) {
		this.length = length;
		computeCoordinates();
	}

	public void setDirection(double direction) {
		this.direction = MathUtils.normalizeAngle(direction);
		computeCoordinates();
	}

	public void computeCoordinates() {
		x = Math.cos(direction) * length;
		y = Math.sin(direction) * length;
	}
}
