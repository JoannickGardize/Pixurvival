package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.Entity;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.livingEntity.CreatureEntity;

public class GetAwayBehavior extends Behavior {

	private static final long serialVersionUID = 1L;

	@Override
	protected void step(CreatureEntity creature) {
		Entity player = creature.getBehaviorData().getClosestPlayer();
		creature.getAwayFrom(player);
		creature.getBehaviorData().setNextUpdateDelayRelativeToSpeed(CreatureEntity.OBSTACLE_VISION_DISTANCE);
	}
}
