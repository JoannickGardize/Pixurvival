package com.pixurvival.core.util;

import com.pixurvival.core.entity.Entity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PseudoAIUtils {

    /**
     * Recherche un angle ou l'entité passé en paramètre ne devrait pas entrer
     * en collision avec un obstacle, l'angle se veut le plus proche de l'angle
     * cible passé en paramètre. Cette méthode se veut peut gourmande en calcul,
     * de ce fait, elle peut parfois ne pas s'en sortir. Dans le pire des cas,
     * elle retourne un angle aléatoire. Le test de collision se fait par
     * avancement unité par unité, et peut donc ne pas voir un obstacle dans
     * certains cas.
     *
     * @param entity            L'entité que l'on souhaite déplacer
     * @param targetMovingAngle L'angle cible de déplacement
     * @param viewDistance      La distance qui sera testé en ligne droite pour les collisions
     * @param angleGranularity  Le changement d'angle qui sera effectué après un échec sur
     *                          l'angle en cours.
     * @return un angle de déplacement, se voulant le plus proche possible de
     * l'angle souhaité, sans entrer en collision.
     */
    public static float avoidObstacles(Entity entity, float targetMovingAngle, int viewDistance, float angleGranularity) {
        if (!collideInDirection(entity, targetMovingAngle, viewDistance)) {
            return targetMovingAngle;
        }
        int maxLoop = (int) (Math.PI * 2 / angleGranularity);
        float orientation = entity.getWorld().getRandom().nextBoolean() ? 1 : -1;
        for (int i = 1; i < maxLoop; i++) {
            float currentAngle = targetMovingAngle + angleGranularity * (i / 2) * orientation;
            if (!collideInDirection(entity, currentAngle, viewDistance)) {
                return MathUtils.normalizeAngle(currentAngle);
            }
            orientation *= -1;
        }
        return entity.getWorld().getRandom().nextAngle();
    }

    /**
     * @param entity            The entity from which the position of the test starts, and
     *                          considering it's collision radius
     * @param targetMovingAngle The direction that will be tested
     * @param viewDistance      The distance of the test
     * @return true if the way is not free, false otherwise.
     */
    public static boolean collideInDirection(Entity entity, float targetMovingAngle, int viewDistance) {

        Vector2 delta = Vector2.fromEuclidean(1, targetMovingAngle);
        Vector2 testPoint = entity.getPosition().copy();
        for (int i = 0; i < viewDistance; i++) {
            testPoint.add(delta);
            if (entity.getWorld().getMap().collide(testPoint.getX(), testPoint.getY(), entity.getCollisionRadius())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param shooterPosition
     * @param bulletSpeed
     * @param targetPosition  The actual target position, the final target position is
     *                        stored there.
     * @param targetVelocity
     */
    public static void findTargetPositionPrediction(Vector2 shooterPosition, float bulletSpeed, Vector2 targetPosition, Vector2 targetVelocity) {
        float a = (targetVelocity.getX() * targetVelocity.getX()) + (targetVelocity.getY() * targetVelocity.getY()) - (bulletSpeed * bulletSpeed);
        float b = 2 * (targetVelocity.getX() * (targetPosition.getX() - shooterPosition.getX()) + targetVelocity.getY() * (targetPosition.getY() - shooterPosition.getY()));
        float c = ((targetPosition.getX() - shooterPosition.getX()) * (targetPosition.getX() - shooterPosition.getX()))
                + ((targetPosition.getY() - shooterPosition.getY()) * (targetPosition.getY() - shooterPosition.getY()));
        float disc = b * b - (4 * a * c);
        if (disc >= 0) {
            float t1 = (-1 * b + (float) Math.sqrt(disc)) / (2 * a);
            float t2 = (-1 * b - (float) Math.sqrt(disc)) / (2 * a);
            float t = Math.max(t1, t2);
            targetPosition.set((targetVelocity.getX() * t) + targetPosition.getX(), (targetVelocity.getY() * t) + targetPosition.getY());
        }
    }
}
