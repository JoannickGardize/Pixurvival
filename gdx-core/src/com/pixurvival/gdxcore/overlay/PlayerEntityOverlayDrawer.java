package com.pixurvival.gdxcore.overlay;

import com.pixurvival.core.livingEntity.PlayerEntity;

public class PlayerEntityOverlayDrawer extends EntityOverlayStackDrawer<PlayerEntity> {

	public PlayerEntityOverlayDrawer() {
		add(new LifeHungerBarDrawer());
	}
}
