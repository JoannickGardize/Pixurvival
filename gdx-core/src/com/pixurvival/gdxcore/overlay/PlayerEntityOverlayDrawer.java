package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.livingEntity.PlayerEntity;

public class PlayerEntityOverlayDrawer implements OverlayDrawer<PlayerEntity> {

	private EntityOverlayStackDrawer<PlayerEntity> selfDrawer = new EntityOverlayStackDrawer<>();
	private EntityOverlayStackDrawer<PlayerEntity> othersDrawer = new EntityOverlayStackDrawer<>();

	public PlayerEntityOverlayDrawer() {
		selfDrawer.add(new LifeHungerBarDrawer());
		othersDrawer.add(new LifeBarDrawer());
		othersDrawer.add(new NameDrawer());
	}

	@Override
	public void draw(Batch batch, Viewport worldViewport, PlayerEntity e) {
		if (e.equals(e.getWorld().getMyPlayer())) {
			selfDrawer.draw(batch, worldViewport, e);
		} else {
			othersDrawer.draw(batch, worldViewport, e);
		}
	}
}
