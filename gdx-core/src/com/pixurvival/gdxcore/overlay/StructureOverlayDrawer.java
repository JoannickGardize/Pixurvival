package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.map.MapStructure;

public class StructureOverlayDrawer implements OverlayDrawer<MapStructure> {

	private DurationBarDrawer durationBarDrawer = new DurationBarDrawer();

	@Override
	public void draw(Batch batch, Viewport worldViewport, MapStructure e) {
		durationBarDrawer.draw(batch, worldViewport, e);
	}

}
