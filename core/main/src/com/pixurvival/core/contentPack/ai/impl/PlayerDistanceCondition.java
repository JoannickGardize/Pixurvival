package com.pixurvival.core.contentPack.ai.impl;

import com.pixurvival.core.contentPack.ai.BehaviorData;
import com.pixurvival.core.contentPack.ai.ChangeCondition;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerDistanceCondition extends ChangeCondition {

	private static final long serialVersionUID = 1L;

	private DoubleComparison test;
	private double targetDistance;

	@Override
	public boolean test(CreatureEntity creature) {
		BehaviorData data = creature.getBehaviorData();
		double distance = data.getClosestDistanceSquaredToPlayer();
		return test.test(distance, targetDistance);
	}

}
