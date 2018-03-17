package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.Collidable;

public interface EntityDrawer<E extends Collidable> {

	void update(E e);

	void draw(Batch batch, E e);
}
