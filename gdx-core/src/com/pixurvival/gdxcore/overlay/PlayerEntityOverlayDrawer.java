package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.livingEntity.PlayerEntity;

public class PlayerEntityOverlayDrawer implements OverlayDrawer<PlayerEntity> {

	private EntityOverlayStackDrawer<PlayerEntity> selfDrawer = new EntityOverlayStackDrawer<>();
	private EntityOverlayStackDrawer<PlayerEntity> alliesDrawer = new EntityOverlayStackDrawer<>();
	private EntityOverlayStackDrawer<PlayerEntity> ennemiesDrawer = new EntityOverlayStackDrawer<>();

	public PlayerEntityOverlayDrawer() {
		selfDrawer.add(new LifeHungerBarDrawer());
		alliesDrawer.add(new LifeBarDrawer(OverlayConstants.ALLY_LIFE_BAR_COLOR));
		alliesDrawer.add(new NameDrawer());
		ennemiesDrawer.add(new LifeBarDrawer(OverlayConstants.ENNEMY_LIFE_BAR_COLOR));
		ennemiesDrawer.add(new NameDrawer());
	}

	@Override
	public void draw(Batch batch, Viewport worldViewport, PlayerEntity e) {
		PlayerEntity myPlayer = e.getWorld().getMyPlayer();
		if (e.getId() == myPlayer.getId()) {
			selfDrawer.draw(batch, worldViewport, e);
		} else if (e.getTeam().getId() == myPlayer.getTeam().getId()) {
			alliesDrawer.draw(batch, worldViewport, e);
		} else {
			ennemiesDrawer.draw(batch, worldViewport, e);
		}
	}
}
