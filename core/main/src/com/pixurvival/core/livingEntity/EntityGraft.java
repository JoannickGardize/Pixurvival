package com.pixurvival.core.livingEntity;

import com.pixurvival.core.Time;

import lombok.Getter;

public abstract class EntityGraft {

	private @Getter LivingEntity entity;
	private @Getter long termTimeMillis;

	public EntityGraft(LivingEntity entity, double duration) {
		this.entity = entity;
		termTimeMillis = entity.getWorld().getTime().getTimeMillis() + Time.secToMillis(duration);
	}

	public abstract void update();

	public abstract void end();
}
