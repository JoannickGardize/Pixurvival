package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.Time;
import com.pixurvival.core.livingEntity.LivingEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CooldownAbility extends Ability {

	private static final long serialVersionUID = 1L;

	private double cooldown;

	@Override
	public boolean start(LivingEntity entity) {
		return true;
	}

	@Override
	public boolean update(LivingEntity entity) {
		CooldownAbilityData data = ((CooldownAbilityData) getAbilityData(entity));
		return update(entity, data);
	}

	@Override
	public boolean update(LivingEntity entity, AbilityData data) {
		CooldownAbilityData cooldownData = (CooldownAbilityData) data;
		long readyTimeMillis = cooldownData.getReadyTimeMillis();
		long currentTimeMillis = entity.getWorld().getTime().getTimeMillis();
		if (currentTimeMillis >= readyTimeMillis) {
			fire(entity);
			cooldownData.setReadyTimeMillis(currentTimeMillis + Time.secToMillis(cooldown));
		}
		return false;
	}

	@Override
	public boolean stop(LivingEntity entity) {
		return true;
	}

	@Override
	public AbilityData createAbilityData() {
		return new CooldownAbilityData();
	}

	public abstract void fire(LivingEntity entity);
}