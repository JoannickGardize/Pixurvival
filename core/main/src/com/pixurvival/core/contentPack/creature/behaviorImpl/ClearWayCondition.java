package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.util.PseudoAIUtils;

public class ClearWayCondition extends ChangeCondition {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean test(CreatureEntity creature) {
		if (creature.getTargetEntity() == null) {
			return false;
		}
		return !PseudoAIUtils.collideInDirection(creature, creature.angleToward(creature.getTargetEntity()), (int) CreatureEntity.OBSTACLE_VISION_DISTANCE);
	}

}
