package com.pixurvival.core.livingEntity.alteration;

import java.io.Serializable;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.stats.StatType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersistentStatAlteration implements PersistentAlteration, Serializable {

	private static final long serialVersionUID = 1L;

	private StatType statType;
	private StatAlterationOperation operation;
	private float value;

	@Override
	public void apply(LivingEntity entity) {
		entity.getStats().addAlteration(this);
	}

	@Override
	public void supply(LivingEntity entity) {
		entity.getStats().removeAlteration(this);
	}
}
