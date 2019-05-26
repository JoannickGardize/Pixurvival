package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.gdxcore.PixurvivalGame;

public abstract class EntityDrawer<E extends Entity> implements ElementDrawer<E> {

	@Deprecated
	public void updateOld(E e) {
		Object o = e.getCustomData();
		if (o == null) {
			o = new DrawData();
			((DrawData) o).getDrawPosition().set(e.getPosition());
			e.setCustomData(o);
		}
		DrawData data = (DrawData) o;
		Vector2 drawPos = data.getDrawPosition();
		Vector2 targetPosition = new Vector2(e.getVelocity()).mul(PixurvivalGame.getInterpolationTime()).add(e.getPosition());
		drawPos.lerp(targetPosition, 0.7);

	}

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
		double speed = Math.max(e.getSpeed(), 2);
		double deltaSpeed = speed * Gdx.graphics.getDeltaTime();
		if (distance > 5 * 5 || distance <= deltaSpeed * deltaSpeed) {
			drawPos.set(position);
		} else {
			double step = speed + (distance / (5 * 5)) * (speed * 3);
			double angle = drawPos.angleToward(position);
			// reuse of position instance
			drawPos.add(position.setFromEuclidean(step * Gdx.graphics.getDeltaTime(), angle));
		}
	}

	@Override
	public void drawDebug(ShapeRenderer renderer, E e) {
		renderer.setColor(Color.WHITE);
		renderer.circle((float) e.getPosition().getX(), (float) e.getPosition().getY(), (float) e.getCollisionRadius(), 16);
	}
}
