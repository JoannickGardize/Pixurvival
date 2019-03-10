package com.pixurvival.core.aliveEntity.ability;

import com.pixurvival.core.World;
import com.pixurvival.core.aliveEntity.PlayerEntity;

public abstract class WorkAbility extends Ability<PlayerEntity> {

	@Override
	public boolean canMove() {
		return false;
	}

	@Override
	public void start(PlayerEntity entity) {
	}

	@Override
	public boolean update(PlayerEntity entity) {
		World world = entity.getWorld();
		WorkAbilityData data = (WorkAbilityData) getAbilityData(entity);
		if (data.getDuration() - data.getStartTime() >= world.getTime().getTime()) {
			entity.setActivity(Activity.NONE);
			finished(entity);
			return true;
		}
		return false;
	}

	@Override
	public void stop(PlayerEntity entity) {
	}

	public abstract void finished(PlayerEntity entity);
}
