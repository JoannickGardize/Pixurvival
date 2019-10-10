package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.stats.StatModifier;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatAlteration extends PersistentAlteration {

	private static final long serialVersionUID = 1L;

	private StatModifier statModifier = new StatModifier();

	@Override
	public void begin(TeamMember source, LivingEntity entity) {
		entity.getStats().addModifier(statModifier);
	}

	@Override
	public void update(TeamMember source, LivingEntity entity) {
	}

	@Override
	public void end(TeamMember source, LivingEntity entity) {
		entity.getStats().removeModifier(statModifier);
	}
}
