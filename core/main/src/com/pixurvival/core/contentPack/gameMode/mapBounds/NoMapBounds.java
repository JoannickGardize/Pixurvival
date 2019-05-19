package com.pixurvival.core.contentPack.gameMode.mapBounds;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.Rectangle;
import com.pixurvival.core.livingEntity.LivingEntity;

public class NoMapBounds implements MapBounds {

	private static final long serialVersionUID = 1L;

	@Override
	public Rectangle getRectangle(World world) {
		return null;
	}

	@Override
	public void updateOutsideEntity(LivingEntity entity) {
	}

}
