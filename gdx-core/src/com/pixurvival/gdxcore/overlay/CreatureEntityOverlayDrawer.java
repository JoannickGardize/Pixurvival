package com.pixurvival.gdxcore.overlay;

import com.pixurvival.core.livingEntity.PlayerEntity;

public class CreatureEntityOverlayDrawer extends EntityOverlayStackDrawer<PlayerEntity> {

	public CreatureEntityOverlayDrawer() {
		add(new LifeBarDrawer());
	}
}
