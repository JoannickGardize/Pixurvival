package com.pixurvival.core.livingEntity.alteration;

import java.util.function.Consumer;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContinuousDamageAlteration extends Alteration {

	private static final long serialVersionUID = 1L;

	private StatFormula damagePerSecond = new StatFormula();

	@Override
	public void targetedApply(TeamMember source, LivingEntity target) {
		target.takeDamage(damagePerSecond.getValue(source.getStats()) * (float) target.getWorld().getTime().getDeltaTime());
	}

	@Override
	public void forEachStatFormulas(Consumer<StatFormula> action) {
		action.accept(damagePerSecond);
	}
}
