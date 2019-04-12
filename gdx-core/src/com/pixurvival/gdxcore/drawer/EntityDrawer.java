package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.Gdx;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.gdxcore.PixurvivalGame;

public abstract class EntityDrawer<E extends Entity> implements ElementDrawer<E> {

	@Override
	public void update(E e) {
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
			double angle = drawPos.angleToward(position);
			// reuse of position instance
			drawPos.add(position.setFromEuclidean(speed * Gdx.graphics.getRawDeltaTime(), angle));
		}
	}
}
