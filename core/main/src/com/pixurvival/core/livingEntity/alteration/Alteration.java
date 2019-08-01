package com.pixurvival.core.livingEntity.alteration;

import java.io.Serializable;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

public interface Alteration extends Serializable {

	/**
	 * Apply this alteration to the Entity.
	 * 
	 * @param source
	 *            The source chain of this alteration.
	 * @param entity
	 */
	void apply(TeamMember source, LivingEntity entity);
}