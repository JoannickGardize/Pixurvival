package com.pixurvival.core.contentPack.gameMode.mapBounds;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.Rectangle;
import com.pixurvival.core.livingEntity.LivingEntity;

public interface MapBounds {

	/**
	 * @return The Rectangle representing the bounds of the map, or null if
	 *         there is no bounds.
	 */
	Rectangle getRectangle(World world);

	/**
	 * Called every ticks for each LivingEntity outside of the bounds.
	 * 
	 * @param entity
	 */
	void updateOutsideEntity(LivingEntity entity);
}
