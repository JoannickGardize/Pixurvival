package com.pixurvival.core.aliveEntity.creature.ai;

import com.pixurvival.core.aliveEntity.creature.CreatureEntity;

public abstract class ChangeCondition {

	private Behavior nextBehavior;

	public abstract boolean test(CreatureEntity creatureEntity);
}
