package com.pixurvival.core;

import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.util.Plugin;
import com.pixurvival.core.util.Rectangle;

public class MapLimitsManager implements Plugin<World> {

	@Override
	public void update(World world) {
		MapLimits mapLimits = world.getMapLimits();
		Rectangle rectangle = mapLimits.getRectangle();
		for (Entity e : world.getEntityPool().get(EntityGroup.PLAYER)) {
			if (!rectangle.contains(e.getPosition())) {
				((LivingEntity) e).takeTrueDamage(mapLimits.getTrueDamagePerSecond() * world.getTime().getDeltaTime());
			}
		}
	}
}
