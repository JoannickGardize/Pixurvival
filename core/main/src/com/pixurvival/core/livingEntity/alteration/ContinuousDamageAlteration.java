package com.pixurvival.core.livingEntity.alteration;

import java.util.function.Consumer;

import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContinuousDamageAlteration extends Alteration {

	private static final long serialVersionUID = 1L;

	private StatFormula damagePerSecond = new StatFormula();

	@Override
	public void targetedApply(TeamMember source, TeamMember target) {
		target.takeDamage(damagePerSecond.getValue(source.getStats()) * target.getWorld().getTime().getDeltaTime());
	}

	@Override
	public void forEachStatFormulas(Consumer<StatFormula> action) {
		action.accept(damagePerSecond);
	}
}
