package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.livingEntity.LivingEntity;

public interface PersistentAlteration extends Alteration {

	void supply(LivingEntity entity);
}
