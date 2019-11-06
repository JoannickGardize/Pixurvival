package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContinuousDamageAlteration extends Alteration {

	private static final long serialVersionUID = 1L;

	private StatAmount damagePerSecond = new StatAmount();

	@Override
	public void targetedApply(TeamMember source, LivingEntity target) {
		target.takeDamage(damagePerSecond.getValue(source.getStats()) * (float) target.getWorld().getTime().getDeltaTime());
	}
}
