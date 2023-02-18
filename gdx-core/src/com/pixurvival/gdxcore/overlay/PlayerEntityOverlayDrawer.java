package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.gdxcore.PixurvivalGame;

public class PlayerEntityOverlayDrawer implements OverlayDrawer<PlayerEntity> {

	private EntityOverlayStackDrawer<PlayerEntity> spectatedPlayerDrawer = new EntityOverlayStackDrawer<>();
	private EntityOverlayStackDrawer<PlayerEntity> selfDrawer = new EntityOverlayStackDrawer<>();
	private EntityOverlayStackDrawer<PlayerEntity> alliesDrawer = new EntityOverlayStackDrawer<>();
	private EntityOverlayStackDrawer<PlayerEntity> enemiesDrawer = new EntityOverlayStackDrawer<>();

	public PlayerEntityOverlayDrawer() {
		spectatedPlayerDrawer.add(new LifeHungerBarDrawer());
		spectatedPlayerDrawer.add(new RoleDrawer());
		spectatedPlayerDrawer.add(new NameDrawer());
		spectatedPlayerDrawer.add(new WorkingBarDrawer());

		selfDrawer.add(new LifeHungerBarDrawer());
		selfDrawer.add(new RoleDrawer());
		selfDrawer.add(new WorkingBarDrawer());

		alliesDrawer.add(new LifeBarDrawer());
		alliesDrawer.add(new RoleDrawer());
		alliesDrawer.add(new NameDrawer());
		alliesDrawer.add(new WorkingBarDrawer());

		enemiesDrawer.add(new LifeBarDrawer());
		enemiesDrawer.add(new RoleDrawer());
		enemiesDrawer.add(new NameDrawer());
	}

	@Override
	public void draw(Batch batch, Viewport worldViewport, PlayerEntity e) {
		PlayerEntity myPlayer = e.getWorld().getMyPlayer();
		if (e.getId() == myPlayer.getId()) {
			if (PixurvivalGame.getClient().isSpectator()) {
				spectatedPlayerDrawer.draw(batch, worldViewport, myPlayer);
			} else {
				selfDrawer.draw(batch, worldViewport, e);
			}
		} else if (e.getTeam().getId() == myPlayer.getTeam().getId()) {
			alliesDrawer.draw(batch, worldViewport, e);
		} else {
			enemiesDrawer.draw(batch, worldViewport, e);
		}
	}
}
