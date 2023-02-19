package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.Positionnable;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveTowardBehavior extends AbstractMoveTowardBehavior {

	private static final long serialVersionUID = 1L;

	private BehaviorTarget targetType;

	@Override
	protected Positionnable findTarget(CreatureEntity creature) {
		return targetType.getEntityGetter().apply(creature);
	}
}
