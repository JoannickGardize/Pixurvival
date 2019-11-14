package com.pixurvival.core.livingEntity.alteration;

import java.io.Serializable;
import java.util.function.Consumer;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Alteration implements Serializable {

	private static final long serialVersionUID = 1L;

	private AlterationTarget targetType = AlterationTarget.TARGET;

	/**
	 * Apply an alteration to a target.
	 * 
	 * @param source
	 *            The source of this alteration.
	 * @param entity
	 *            The entity targeted by the alteration
	 */
	public void apply(TeamMember source, LivingEntity target) {
		targetedApply(source, (LivingEntity) targetType.getFunction().apply(source, target));
	}

	public abstract void targetedApply(TeamMember source, LivingEntity target);

	public void forEachStatFormulas(Consumer<StatFormula> action) {
		// for override
	}
}