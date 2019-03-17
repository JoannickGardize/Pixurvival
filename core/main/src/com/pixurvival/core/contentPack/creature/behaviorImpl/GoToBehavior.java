package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.Entity;
import com.pixurvival.core.Time;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoToBehavior extends Behavior {

	private static final long serialVersionUID = 1L;

	private double minDistance;

	@Override
	protected void step(CreatureEntity creature) {
		Entity player = creature.getBehaviorData().getClosestPlayer();
		if (creature.getBehaviorData().getClosestDistanceSquaredToPlayer() > minDistance * minDistance) {
			creature.moveToward(player);
			creature.getBehaviorData().setNextUpdateDelay(Time.secToMillis(CreatureEntity.OBSTACLE_VISION_DISTANCE / creature.getSpeed()));
		} else {
			creature.setForward(false);
		}
	}
}
