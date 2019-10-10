package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContinuousDamageAlteration implements Alteration {

	private static final long serialVersionUID = 1L;

	private StatAmount damagePerSecond = new StatAmount();

	@Override
	public void apply(TeamMember source, LivingEntity entity) {
		entity.takeDamage(damagePerSecond.getValue(source.getStats()) * (float) entity.getWorld().getTime().getDeltaTime());
	}
}
