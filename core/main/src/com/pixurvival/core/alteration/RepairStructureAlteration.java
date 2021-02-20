package com.pixurvival.core.alteration;

import java.util.function.Consumer;

import com.pixurvival.core.Healable;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.team.TeamMember;

public class RepairStructureAlteration extends UniqueAlteration {

	private static final long serialVersionUID = 1L;

	@Valid
	private StatFormula amount = new StatFormula();

	@Override
	public void uniqueApply(TeamMember source, TeamMember entity) {
		if (entity instanceof Healable) {
			((Healable) entity).takeHeal(amount.getValue(source.getStats()));
		}
	}

	@Override
	public void forEachStatFormulas(Consumer<StatFormula> action) {
		action.accept(amount);
	}
}
