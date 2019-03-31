package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerDistanceCondition extends ChangeCondition {

	private static final long serialVersionUID = 1L;

	@Required
	private DoubleComparison test;

	@Bounds(min = 0)
	private double targetDistance;

	@Override
	public boolean test(CreatureEntity creature) {
		BehaviorData data = creature.getBehaviorData();
		double distance = data.getClosestDistanceSquaredToPlayer();
		return test.test(distance, targetDistance);
	}

}
