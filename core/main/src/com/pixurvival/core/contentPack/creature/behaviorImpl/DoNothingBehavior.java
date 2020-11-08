package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoNothingBehavior extends Behavior {

	private static final long serialVersionUID = 1L;

	private BehaviorTarget targetType = BehaviorTarget.NONE;

	@Override
	public void begin(CreatureEntity creature) {
		super.begin(creature);
		creature.setForward(false);
	}

	@Override
	protected void step(CreatureEntity creature) {
		creature.setTargetEntity(targetType.getEntityGetter().apply(creature));
		creature.getBehaviorData().setNextUpdateDelayMillis(BehaviorData.DEFAULT_STANDBY_DELAY);
	}

}
