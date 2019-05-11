package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.entity.Entity;

public interface OverlayStackElementDrawer<E extends Entity> {

	/**
	 * @param batch
	 * @param infos
	 * @param e
	 * @return The height of the element
	 */
	float draw(Batch batch, OverlayInfos infos, E e);
}
