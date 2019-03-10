package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.LivingEntity;

public abstract class WorkAbility extends Ability {

	@Override
	public boolean canMove() {
		return false;
	}

	@Override
	public boolean start(LivingEntity entity) {
		if (entity.getWorld().isServer()) {
			((WorkAbilityData) getAbilityData(entity)).setStartTime(entity.getWorld().getTime().getTime());
		}
		return true;
	}

	@Override
	public boolean update(LivingEntity entity) {
		World world = entity.getWorld();
		WorkAbilityData data = (WorkAbilityData) getAbilityData(entity);
		if (world.getTime().getTime() - data.getStartTime() >= data.getDuration()) {
			workFinished(entity);
			return true;
		}
		return false;
	}

	@Override
	public boolean stop(LivingEntity entity) {
		return true;
	}

	public abstract void workFinished(LivingEntity entity);
}
