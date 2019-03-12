package com.pixurvival.core.contentPack.ai.impl;

import com.pixurvival.core.contentPack.ai.ChangeCondition;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeCondition extends ChangeCondition {

	private static final long serialVersionUID = 1L;

	private long targetTimeMillis;

	@Override
	public boolean test(CreatureEntity creature) {
		return creature.getBehaviorData().getElapsedTimeMillis() >= targetTimeMillis;
	}

}
