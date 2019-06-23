package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeCondition extends ChangeCondition {

	private static final long serialVersionUID = 1L;

	@Bounds(min = 0)
	private long targetTime;

	@Override
	public boolean test(CreatureEntity creature) {
		return creature.getBehaviorData().getElapsedTimeMillis() >= targetTime;
	}
}
