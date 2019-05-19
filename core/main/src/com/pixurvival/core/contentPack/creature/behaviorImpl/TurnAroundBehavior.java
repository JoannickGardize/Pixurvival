package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurnAroundBehavior extends Behavior {

	private static final long serialVersionUID = 1L;

	@Bounds(min = 0)
	private double minDistance;

	@Bounds(min = 0)
	private double maxDistance;

	@Override
	protected void step(CreatureEntity creature) {
		Entity target = creature.getBehaviorData().getClosestEnnemy();
		if (target == null) {
			creature.setForward(false);
			creature.getBehaviorData().setNextUpdateDelayMillis(BehaviorData.DEFAULT_STANDBY_DELAY);
		} else {
			double closestDistanceSquared = creature.getBehaviorData().getClosestDistanceSquaredToEnnemy();
			if (closestDistanceSquared > maxDistance * maxDistance) {
				creature.moveToward(target);
			} else if (closestDistanceSquared < minDistance * minDistance) {
				creature.getAwayFrom(target);
			} else {
				double aroundAngle = creature.getWorld().getRandom().nextBoolean() ? Math.PI / 2 : -Math.PI / 2;
				creature.move(creature.angleToward(target) + aroundAngle);
			}
			creature.getBehaviorData().setNextUpdateDelayRelativeToSpeed(CreatureEntity.OBSTACLE_VISION_DISTANCE);
		}
		creature.setTargetEntity(target);
	}

}
