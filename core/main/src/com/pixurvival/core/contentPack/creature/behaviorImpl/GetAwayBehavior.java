package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.livingEntity.CreatureEntity;

public class GetAwayBehavior extends Behavior {

	private static final long serialVersionUID = 1L;

	@Override
	protected void step(CreatureEntity creature) {
		Entity ennemy = creature.getBehaviorData().getClosestEnnemy();
		if (ennemy == null) {
			creature.setForward(false);
			creature.getBehaviorData().setNextUpdateDelayMillis(BehaviorData.DEFAULT_STANDBY_DELAY);
		} else {
			creature.getAwayFrom(ennemy);
			creature.getBehaviorData().setNextUpdateDelayRelativeToSpeed(CreatureEntity.OBSTACLE_VISION_DISTANCE);
		}
		creature.setTargetEntity(ennemy);
	}
}
