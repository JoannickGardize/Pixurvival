package com.pixurvival.core.contentPack.creature.changeConditionImpl;

import com.pixurvival.core.Damageable;
import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.BehaviorTarget;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.util.FloatComparison;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HealthCondition extends ChangeCondition {

	private static final long serialVersionUID = 1L;

	private BehaviorTarget targetType;
	private FloatComparison operator;
	@Bounds(min = 0, max = 1, maxInclusive = true)
	private float percentValue;

	@Override
	public boolean test(CreatureEntity creature) {
		Entity entity = targetType.getEntityGetter().apply(creature);
		if (entity instanceof Damageable) {
			return operator.test(((Damageable) entity).getPercentHealth(), percentValue);
		} else {
			return false;
		}
	}
}
