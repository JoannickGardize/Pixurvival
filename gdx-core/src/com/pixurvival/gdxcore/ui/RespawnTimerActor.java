package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityPoolListener;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.gdxcore.PixurvivalGame;

public class RespawnTimerActor extends Actor implements EntityPoolListener {

	private long respawnTime;
	private long actualRespawnCount = Long.MAX_VALUE;
	private GlyphLayout actualGlyph;

	public RespawnTimerActor() {
		setVisible(false);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		updateGlyph();
		if (actualGlyph != null) {
			PixurvivalGame.getOverlayFont().draw(batch, actualGlyph, getX() - actualGlyph.width / 2, getY() + actualGlyph.height);
		}
	}

	@Override
	public void entityAdded(Entity e) {
	}

	@Override
	public void entityRemoved(Entity e) {
	}

	@Override
	public void sneakyEntityRemoved(Entity e) {
	}

	@Override
	public void playerDied(PlayerEntity player) {

		if (player == PixurvivalGame.getClient().getMyOriginalPlayerEntity() && player.getRespawnTime() != -1) {
			setVisible(true);
			this.respawnTime = player.getRespawnTime();
			actualRespawnCount = Long.MAX_VALUE;
		}
	}

	@Override
	public void playerRespawned(PlayerEntity player) {
		if (player == PixurvivalGame.getClient().getMyOriginalPlayerEntity()) {
			setVisible(false);
		}
	}

	private void updateGlyph() {
		long newRespawnCount = (long) Math.ceil((respawnTime - PixurvivalGame.getWorld().getTime().getTimeMillis()) / 1000.0);
		if (newRespawnCount < actualRespawnCount && newRespawnCount >= 0) {
			actualRespawnCount = newRespawnCount;
			actualGlyph = new GlyphLayout(PixurvivalGame.getOverlayFont(), PixurvivalGame.getString("hud.respawnIn", actualRespawnCount));
		}
	}
}
