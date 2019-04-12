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
public class MoveTowardBehavior extends Behavior {

	private static final long serialVersionUID = 1L;

	@Bounds(min = 0)
	private double minDistance;

	@Override
	protected void step(CreatureEntity creature) {
		Entity player = creature.getBehaviorData().getClosestPlayer();
		if (creature.getBehaviorData().getClosestDistanceSquaredToPlayer() > minDistance * minDistance) {
			creature.moveToward(player);
			creature.getBehaviorData().setNextUpdateDelayRelativeToSpeed(CreatureEntity.OBSTACLE_VISION_DISTANCE);
		} else {
			creature.setForward(false);
			creature.getBehaviorData().setNextUpdateDelayMillis(BehaviorData.DEFAULT_STANDBY_DELAY);
		}
	}
}
