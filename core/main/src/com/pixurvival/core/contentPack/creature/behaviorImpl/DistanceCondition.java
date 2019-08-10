package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistanceCondition extends ChangeCondition {

	private static final long serialVersionUID = 1L;

	private BehaviorTarget targetType;

	@Required
	private DoubleComparison operator;

	@Bounds(min = 0)
	private double targetDistance;

	@Override
	public boolean test(CreatureEntity creature) {
		Entity target = targetType.getEntityGetter().apply(creature);
		double distance = creature.nullSafeDistanceSquared(target);
		return operator.test(distance, targetDistance * targetDistance);
	}

}
