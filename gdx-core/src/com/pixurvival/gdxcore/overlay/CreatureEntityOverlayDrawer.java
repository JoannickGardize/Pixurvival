package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;

public class CreatureEntityOverlayDrawer implements OverlayDrawer<CreatureEntity> {

	private EntityOverlayStackDrawer<CreatureEntity> alliesDrawer = new EntityOverlayStackDrawer<>();
	private EntityOverlayStackDrawer<CreatureEntity> ennemiesDrawer = new EntityOverlayStackDrawer<>();

	public CreatureEntityOverlayDrawer() {
		alliesDrawer.add(new LifeBarDrawer(OverlayConstants.ALLY_LIFE_BAR_COLOR));
		ennemiesDrawer.add(new LifeBarDrawer(OverlayConstants.ENNEMY_LIFE_BAR_COLOR));
	}

	@Override
	public void draw(Batch batch, Viewport worldViewport, CreatureEntity e) {
		PlayerEntity myPlayer = e.getWorld().getMyPlayer();
		if (e.getTeam().getId() == myPlayer.getTeam().getId()) {
			alliesDrawer.draw(batch, worldViewport, e);
		} else {
			ennemiesDrawer.draw(batch, worldViewport, e);
		}
	}
}
