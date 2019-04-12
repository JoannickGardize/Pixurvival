package com.pixurvival.core.livingEntity.alteration;

import java.io.Serializable;

import com.pixurvival.core.livingEntity.LivingEntity;

public interface Alteration extends Serializable {

	void apply(Object source, LivingEntity entity);
}