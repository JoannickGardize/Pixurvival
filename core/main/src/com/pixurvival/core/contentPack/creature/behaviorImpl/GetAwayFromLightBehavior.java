package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.map.Light;

public class GetAwayFromLightBehavior extends Behavior {

	private static final long serialVersionUID = 1L;

	@Override
	protected void step(CreatureEntity creature) {
		Light light = (Light) creature.getBehaviorData().getCustomData();
		if (light == null) {
			light = creature.getWorld().getMap().getAnyCollidingLight(creature.getPosition());
			creature.getBehaviorData().setCustomData(light);
		}
		if (light == null) {
			creature.setForward(false);
			creature.getBehaviorData().setNextUpdateDelayMillis(BehaviorData.DEFAULT_STANDBY_DELAY);
		} else {
			creature.getAwayFrom(light);
			creature.getBehaviorData().setNextUpdateDelayRelativeToSpeed(CreatureEntity.OBSTACLE_VISION_DISTANCE);
		}
	}
}