package com.pixurvival.core.contentPack.creature.changeConditionImpl;

import com.pixurvival.core.Damageable;
import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.creature.behaviorImpl.BehaviorTarget;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.livingEntity.CreatureEntity;

// TODO ContentPackEditor
public class HealthCondition extends ChangeCondition {

	private static final long serialVersionUID = 1L;

	private BehaviorTarget targetType;
	private FloatComparison operator;
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
