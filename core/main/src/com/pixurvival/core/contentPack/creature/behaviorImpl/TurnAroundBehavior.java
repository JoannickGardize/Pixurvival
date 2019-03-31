package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.Entity;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
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
		Entity player = creature.getBehaviorData().getClosestPlayer();
		if (player == null) {
			creature.setForward(false);
			creature.getBehaviorData().setNextUpdateDelayMillis(BehaviorData.DEFAULT_STANDBY_DELAY);
			return;
		}
		double closestDistanceSquared = creature.getBehaviorData().getClosestDistanceSquaredToPlayer();
		if (closestDistanceSquared > maxDistance * maxDistance) {
			creature.moveToward(player);
		} else if (closestDistanceSquared < minDistance * minDistance) {
			creature.getAwayFrom(player);
		} else {
			double aroundAngle = creature.getWorld().getRandom().nextBoolean() ? Math.PI / 2 : -Math.PI / 2;
			creature.move(creature.angleToward(player) + aroundAngle);
		}
		creature.getBehaviorData().setNextUpdateDelayRelativeToSpeed(CreatureEntity.OBSTACLE_VISION_DISTANCE);
	}

}
