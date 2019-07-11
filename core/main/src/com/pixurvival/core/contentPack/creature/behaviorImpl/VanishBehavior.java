package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.CreatureEntity;

public class VanishBehavior extends Behavior {

	private static final long serialVersionUID = 1L;

	@Override
	protected void step(CreatureEntity creature) {
		Entity closestPlayer = creature.findClosest(EntityGroup.PLAYER);
		if (closestPlayer == null) {
			creature.setAlive(false);
			return;
		}
		if (closestPlayer.getPosition().insideSquare(creature.getPosition(), GameConstants.PLAYER_VIEW_DISTANCE)) {
			creature.getAwayFrom(closestPlayer);
			creature.getBehaviorData().setNextUpdateDelayRelativeToSpeed(CreatureEntity.OBSTACLE_VISION_DISTANCE);
		} else {
			creature.setAlive(false);
		}
	}

}
