package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveTowardBehavior extends Behavior {

	private static final long serialVersionUID = 1L;

	private BehaviorTarget targetType;

	@Positive
	private float minDistance;

	private float randomAngle;

	@Override
	public void begin(CreatureEntity creature) {
		super.begin(creature);
		Entity target = targetType.getEntityGetter().apply(creature);
		creature.setTargetEntity(target);
	}

	@Override
	protected void step(CreatureEntity creature) {
		Entity target = targetType.getEntityGetter().apply(creature);
		if (target != null) {
			float distanceSquared = creature.distanceSquared(target);
			if (distanceSquared > minDistance * minDistance) {
				float distance = (float) Math.sqrt(distanceSquared);
				creature.moveTowardPrecisely(target, distance);
				creature.getBehaviorData().setNextUpdateDelayRelativeToSpeed(distance - minDistance);
				creature.getTargetPosition().set(target.getPosition());
			} else {
				stop(creature);
				creature.getBehaviorData().setTaskFinished(true);
			}
		} else {
			stop(creature);
			creature.getBehaviorData().setNothingToDo(true);
		}
		creature.setTargetEntity(target);
	}

	private void stop(CreatureEntity creature) {
		creature.setForward(false);
		creature.getBehaviorData().setNextUpdateDelayMillis(BehaviorData.DEFAULT_STANDBY_DELAY);
	}
}
