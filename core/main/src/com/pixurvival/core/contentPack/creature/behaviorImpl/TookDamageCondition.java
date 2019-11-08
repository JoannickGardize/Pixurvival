package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.livingEntity.CreatureEntity;

public class TookDamageCondition extends ChangeCondition {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean test(CreatureEntity creature) {
		return creature.getBehaviorData().isTookDamage();
	}

}