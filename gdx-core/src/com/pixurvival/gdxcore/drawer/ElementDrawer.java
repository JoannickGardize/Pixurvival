package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pixurvival.core.Body;

public interface ElementDrawer<E extends Body> {

	void update(E e);

	void drawShadow(Batch batch, E e);

	void backgroundDraw(Batch batch, E e);

	void draw(Batch batch, E e);

	void frontDraw(Batch batch, E e);

	void drawDebug(ShapeRenderer renderer, E e);

}
