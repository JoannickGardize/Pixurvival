package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.Collidable;

public interface ElementDrawer<E extends Collidable> {

	void update(E e);

	void drawShadow(Batch batch, E e);

	void draw(Batch batch, E e);

	void topDraw(Batch batch, E e);

}
