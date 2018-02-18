package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.Entity;

public interface EntityDrawer<E extends Entity> {

	void draw(Batch batch, E e);
}
