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

	private BehaviorTarget targetType;

	@Bounds(min = 0)
	private float minDistance;

	@Bounds(min = 0)
	private float maxDistance;

	@Override
	public void begin(CreatureEntity creature) {
		Entity target = targetType.getEntityGetter().apply(creature);
		creature.setTargetEntity(target);
		super.begin(creature);
	}

	@Override
	protected void step(CreatureEntity creature) {
		Entity target = targetType.getEntityGetter().apply(creature);
		if (target == null) {
			creature.setForward(false);
			creature.getBehaviorData().setNextUpdateDelayMillis(BehaviorData.DEFAULT_STANDBY_DELAY);
		} else {
			float distanceSquared = creature.distanceSquared(target);
			if (distanceSquared > maxDistance * maxDistance) {
				creature.moveToward(target);
			} else if (distanceSquared < minDistance * minDistance) {
				creature.getAwayFrom(target);
			} else {
				float aroundAngle = creature.getWorld().getRandom().nextBoolean() ? (float) Math.PI / 2 : -(float) Math.PI / 2;
				creature.move(creature.angleToward(target) + aroundAngle);
			}
			creature.getBehaviorData().setNextUpdateDelayRelativeToSpeed(CreatureEntity.OBSTACLE_VISION_DISTANCE);
		}
		creature.setTargetEntity(target);
	}

}
