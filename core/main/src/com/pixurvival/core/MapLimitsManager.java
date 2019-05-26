package com.pixurvival.core;

import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.util.Plugin;
import com.pixurvival.core.util.Rectangle;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MapLimitsManager implements Plugin<World> {

	private Rectangle rectangle;
	private float trueDamagePerSecond;

	@Override
	public void update(World world) {
		for (Entity e : world.getEntityPool().get(EntityGroup.PLAYER)) {
			if (!rectangle.contains(e.getPosition())) {
				((LivingEntity) e).takeTrueDamage(trueDamagePerSecond * (float) world.getTime().getDeltaTime());
			}
		}
	}
}
