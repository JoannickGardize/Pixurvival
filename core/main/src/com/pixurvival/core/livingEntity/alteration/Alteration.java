package com.pixurvival.core.livingEntity.alteration;

import java.io.Serializable;

import com.pixurvival.core.entity.SourceProvider;
import com.pixurvival.core.livingEntity.LivingEntity;

public interface Alteration extends Serializable {

	/**
	 * Apply this alteration to the Entity.
	 * 
	 * @param source
	 *            The source chain of this alteration.
	 * @param entity
	 */
	void apply(SourceProvider source, LivingEntity entity);
}