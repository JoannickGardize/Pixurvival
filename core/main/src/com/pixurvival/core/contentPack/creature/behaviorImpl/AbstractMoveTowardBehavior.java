package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.Positionnable;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractMoveTowardBehavior extends Behavior {

	private static final long serialVersionUID = 1L;

	@Positive
	private float minDistance;

	private float randomAngle;

	protected abstract Positionnable findTarget(CreatureEntity creature);

	@Override
	public void begin(CreatureEntity creature) {
		super.begin(creature);
		Positionnable target = findTarget(creature);
		if (target instanceof Entity) {
			creature.setTargetEntity((Entity) target);
		}
	}

	@Override
	protected void step(CreatureEntity creature) {
		Positionnable target = findTarget(creature);
		if (target != null) {
			float distanceSquared = creature.distanceSquared(target);
			if (distanceSquared > minDistance * minDistance) {
				float distance = (float) Math.sqrt(distanceSquared);
				creature.moveTowardPrecisely(target, distance, randomAngle);
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
		if (target instanceof Entity) {
			creature.setTargetEntity((Entity) target);
		}
	}

	private void stop(CreatureEntity creature) {
		creature.setForward(false);
		creature.getBehaviorData().setNextUpdateDelayMillis(BehaviorData.DEFAULT_STANDBY_DELAY);
	}
}
