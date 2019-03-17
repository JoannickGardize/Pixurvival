package com.pixurvival.core.util;

import com.pixurvival.core.Entity;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MovePathUtils {

	public static void avoidObstacles(Entity entity, double targetMovingAngle, int viewDistance, double angleGranularity) {
		if (!collideinDirection(entity, targetMovingAngle, viewDistance)) {
			entity.setMovingAngle(targetMovingAngle);
			return;
		}
		int maxLoop = (int) (Math.PI * 2 / angleGranularity);
		double orientation = entity.getWorld().getRandom().nextBoolean() ? 1 : -1;
		for (int i = 0; i < maxLoop * 2; i++) {
			double currentAngle = targetMovingAngle + angleGranularity * (i / 2) * orientation;
			if (!collideinDirection(entity, currentAngle, viewDistance)) {
				entity.setMovingAngle(currentAngle);
				return;
			}
			orientation *= -1;
		}
	}

	public static boolean collideinDirection(Entity entity, double targetMovingAngle, int viewDistance) {

		Vector2 delta = Vector2.fromEuclidean(1, targetMovingAngle);
		Vector2 testPoint = entity.getPosition().copy();
		for (int i = 0; i < viewDistance; i++) {
			testPoint.add(delta);
			if (entity.getWorld().getMap().collide(testPoint.getX(), testPoint.getY(), entity.getBoundingRadius())) {
				return true;
			}
		}
		return false;
	}
}
