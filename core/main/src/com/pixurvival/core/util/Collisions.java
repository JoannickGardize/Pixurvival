package com.pixurvival.core.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Collisions {

	public static boolean circleCircle(Vector2 center1, double radius1, Vector2 center2, double radius2) {
		double dx = center1.getX() - center2.getX();
		double dy = center1.getY() - center2.getY();
		double r = radius1 + radius2;
		return dx * dx + dy * dy <= r * r;
	}

	public static boolean dynamicCircleCircle(Vector2 center1, double radius1, Vector2 velocity1, Vector2 center2, double radius2) {
		Vector2 closestPoint = closestPointOnSegment(center1, velocity1, center2);
		return circleCircle(closestPoint, radius1, center2, radius2);
	}

	private static Vector2 closestPointOnSegment(Vector2 startPoint, Vector2 delta, Vector2 p) {
		double dx = delta.getX();
		double dy = delta.getY();

		if (dx == 0 && dy == 0) {
			return new Vector2(startPoint);
		} else {
			double u = ((p.getX() - startPoint.getX()) * dx + (p.getY() - startPoint.getY()) * dy) / (dx * dx + dy * dy);
			if (u < 0) {
				return new Vector2(startPoint);
			} else if (u > 1) {
				return new Vector2(delta);
			} else {
				return new Vector2(startPoint.getX() + u * dx, startPoint.getY() + u * dy);
			}
		}
	}
}
