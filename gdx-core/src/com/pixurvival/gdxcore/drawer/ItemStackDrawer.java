package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.Entity;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.ContentPackTextures;
import com.pixurvival.gdxcore.textures.ItemTexture;
import com.pixurvival.gdxcore.textures.TextureMetrics;

public class ItemStackDrawer implements EntityDrawer<ItemStackEntity> {

	@Override
	public void update(ItemStackEntity e) {

	}

	@Override
	public void draw(Batch batch, ItemStackEntity e) {
		updateDrawPosition(e);
		ContentPackTextures contentPackTextures = PixurvivalGame.getContentPackTextures();
		ItemTexture itemTexture = contentPackTextures.getItem(e.getItemStack().getItem().getId());
		Texture shadow = itemTexture.getShadow();
		Texture texture = itemTexture.getTexture();
		TextureMetrics metrics = itemTexture.getMetrics();
		DrawData data = (DrawData) e.getCustomData();
		Vector2 drawPosition = data.getDrawPosition();
		float x = (float) (drawPosition.x - metrics.getWorldWidth() / 2);
		float y = (float) ((drawPosition.y));
		batch.draw(shadow, x, y - metrics.getWorldWidth() / 4, metrics.getWorldWidth(), metrics.getWorldWidth() / 2);
		batch.draw(texture, (float) (x - metrics.getWorldOffsetX() - contentPackTextures.getTruePixelWidth()),
				(float) (y - contentPackTextures.getTruePixelWidth() - metrics.getWorldOffsetY()), 1, 1);
	}

	private void updateDrawPosition(Entity e) {
		Object o = e.getCustomData();
		if (o == null) {
			o = new DrawData();
			((DrawData) o).getDrawPosition().set(e.getPosition());
			e.setCustomData(o);
		}
		DrawData data = (DrawData) o;
		Vector2 drawPos = data.getDrawPosition();
		Vector2 position = new Vector2(e.getVelocity()).mul(PixurvivalGame.getInterpolationTime()).add(e.getPosition());
		double distance = drawPos.distanceSquared(position);
		double deltaSpeed = e.getSpeed() * Gdx.graphics.getRawDeltaTime();
		if (distance > 5 * 5 || distance <= deltaSpeed * deltaSpeed) {
			drawPos.set(position);
		} else {
			double speed = e.getSpeed() + (distance / (5 * 5)) * (e.getSpeed() * 2);
			double angle = drawPos.angleTo(position);
			// reuse of position instance
			drawPos.add(position.setFromEuclidean(speed * Gdx.graphics.getRawDeltaTime(), angle));
		}
	}
}
