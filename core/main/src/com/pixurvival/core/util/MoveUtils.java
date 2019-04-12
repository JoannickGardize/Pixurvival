package com.pixurvival.core.util;

import com.pixurvival.core.entity.Entity;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MoveUtils {

	/**
	 * Recherche un angle ou l'entité passé en paramètre ne devrait pas entrer en
	 * collision avec un obstacle, l'angle se veut le plus proche de l'angle cible
	 * passé en paramètre. Cette méthode se veut peut gourmande en calcul, de ce
	 * fait, elle peut parfois ne pas s'en sortir. Dans le pire des cas, elle
	 * retourne un angle aléatoire. Le test de collision se fait par avancement
	 * unité par unité, et peut donc ne pas voir un obstacle dans certains cas.
	 * 
	 * @param entity
	 *            L'entité que l'on souhaite déplacer
	 * @param targetMovingAngle
	 *            L'angle cible de déplacement
	 * @param viewDistance
	 *            La distance qui sera testé en ligne droite pour les collisions
	 * @param angleGranularity
	 *            Le changement d'angle qui sera effectué après un échec sur l'angle
	 *            en cours.
	 * @return un angle de déplacement, se voulant le plus proche possible de
	 *         l'angle souhaité, sans entrer en collision.
	 */
	public static double avoidObstacles(Entity entity, double targetMovingAngle, int viewDistance, double angleGranularity) {
		if (!collideInDirection(entity, targetMovingAngle, viewDistance)) {
			return targetMovingAngle;
		}
		int maxLoop = (int) (Math.PI * 2 / angleGranularity);
		double orientation = entity.getWorld().getRandom().nextBoolean() ? 1 : -1;
		for (int i = 1; i < maxLoop; i++) {
			double currentAngle = targetMovingAngle + angleGranularity * (i / 2) * orientation;
			if (!collideInDirection(entity, currentAngle, viewDistance)) {
				return currentAngle;
			}
			orientation *= -1;
		}
		return entity.getWorld().getRandom().nextAngle();
	}

	public static boolean collideInDirection(Entity entity, double targetMovingAngle, int viewDistance) {

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
