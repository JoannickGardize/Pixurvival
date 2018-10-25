package com.pixurvival.core.aliveEntity.creature.ai;

import com.pixurvival.core.aliveEntity.creature.CreatureEntity;

import lombok.Setter;

@Setter
public abstract class Behavior {

	private ChangeCondition[] changeConditions;

	public abstract void update(CreatureEntity entity);

	public void begin(CreatureEntity entity) {
	}

	public void end(CreatureEntity entity) {
	}

	public Behavior nextBehavior(CreatureEntity entity) {
		for (ChangeCondition condition : changeConditions) {
			if (condition.test(entity)) {
				end(entity);

			}
		}
		return null;
	}

}
