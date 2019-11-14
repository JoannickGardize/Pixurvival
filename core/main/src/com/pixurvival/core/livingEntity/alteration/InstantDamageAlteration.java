package com.pixurvival.core.livingEntity.alteration;

import java.util.function.Consumer;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstantDamageAlteration extends UniqueAlteration {

	private static final long serialVersionUID = 1L;

	private StatFormula amount = new StatFormula();

	@Override
	public void uniqueApply(TeamMember source, LivingEntity entity) {
		entity.takeDamage(amount.getValue(source));
	}

	@Override
	public void forEachStatFormulas(Consumer<StatFormula> action) {
		action.accept(amount);
	}
}
