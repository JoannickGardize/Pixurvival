package com.pixurvival.core.contentPack.creature.changeConditionImpl;

import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.BehaviorTarget;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistanceCondition extends ChangeCondition {

	private static final long serialVersionUID = 1L;

	private BehaviorTarget targetType;

	private FloatComparison operator;

	@Positive
	private float targetDistance;

	@Override
	public boolean test(CreatureEntity creature) {
		Entity target = targetType.getEntityGetter().apply(creature);
		float distance = creature.nullSafeDistanceSquared(target);
		return operator.test(distance, targetDistance * targetDistance);
	}
}
