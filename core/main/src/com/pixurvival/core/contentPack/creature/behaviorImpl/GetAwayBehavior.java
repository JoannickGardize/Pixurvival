package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAwayBehavior extends Behavior {

	private static final long serialVersionUID = 1L;

	private BehaviorTarget targetType;

	@Override
	protected void step(CreatureEntity creature) {
		Entity target = targetType.getEntityGetter().apply(creature);
		if (target == null) {
			creature.setForward(false);
			creature.getBehaviorData().setNextUpdateDelayMillis(BehaviorData.DEFAULT_STANDBY_DELAY);
		} else {
			creature.getAwayFrom(target);
			creature.getBehaviorData().setNextUpdateDelayRelativeToSpeed(CreatureEntity.OBSTACLE_VISION_DISTANCE);
		}
		creature.setTargetEntity(target);
	}
}
