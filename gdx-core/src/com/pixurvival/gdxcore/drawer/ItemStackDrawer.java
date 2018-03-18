package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.Entity;
import com.pixurvival.core.ItemStackEntity;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.graphics.ContentPackTextures;

public class ItemStackDrawer implements EntityDrawer<ItemStackEntity> {

	@Override
	public void update(ItemStackEntity e) {

	}

	@Override
	public void draw(Batch batch, ItemStackEntity e) {
		updateDrawPosition(e);
		ContentPackTextures contentPackTextures = PixurvivalGame.getContentPackTextures();
		Texture shadow = contentPackTextures.getShadow(8);
		Texture texture = contentPackTextures.getItem(e.getItemStack().getItem().getId());
		DrawData data = (DrawData) e.getCustomData();
		Vector2 drawPosition = data.getDrawPosition();
		float x = (float) (drawPosition.x - 1.0 / 2);
		float y = (float) ((drawPosition.y /*- e.getBoundingRadius()*/) - contentPackTextures.getTruePixelWidth());
		batch.draw(shadow, x, y - 1f / 4, 1, 1f / 2);
		batch.draw(texture, x, y, 1, 1);
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
