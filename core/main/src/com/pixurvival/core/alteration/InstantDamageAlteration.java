package com.pixurvival.core.alteration;

import java.util.function.Consumer;

import com.pixurvival.core.Damageable;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstantDamageAlteration extends UniqueAlteration {

	private static final long serialVersionUID = 1L;

	@Valid
	private StatFormula amount = new StatFormula();

	@Valid
	private boolean applyToStructures = true;

	private DamageAttributes attributes = new DamageAttributes();

	@Override
	public void uniqueApply(TeamMember source, TeamMember target) {
		if (target instanceof Damageable) {
			((Damageable) target).takeDamage(amount.getValue(source), attributes);
		}
	}

	@Override
	public void forEachStatFormulas(Consumer<StatFormula> action) {
		action.accept(amount);
	}
}
