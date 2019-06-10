package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.Body;

public interface OverlayDrawer<E extends Body> {

	void draw(Batch batch, Viewport worldViewport, E e);
}
