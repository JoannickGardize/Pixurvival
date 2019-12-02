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
		drawPos.lerp(targetPosition, 0.7f);

	}

	@Override
	public void update(E e) {
		DrawData data = (DrawData) e.getCustomData();
		if (data == null) {
			data = new DrawData();
			data.getDrawPosition().set(e.getPosition());
			e.setCustomData(data);
		}
		Vector2 drawPos = data.getDrawPosition();
		Vector2 position = new Vector2(e.getVelocity()).mul(PixurvivalGame.getInterpolationTime()).add(e.getPosition());
		float distance = drawPos.distanceSquared(position);
		float speed = Math.max(e.getSpeed(), Math.max(2, distance));
		float deltaSpeed = speed * Gdx.graphics.getDeltaTime();
		if (distance > 5 * 5 || distance <= deltaSpeed * deltaSpeed) {
			drawPos.set(position);
		} else {
			float step = speed + (distance / (5 * 5)) * (speed * 3);
			float angle = drawPos.angleToward(position);
			drawPos.add(position.setFromEuclidean(step * Gdx.graphics.getDeltaTime(), angle));
		}
	}

	@Override
	public void drawDebug(ShapeRenderer renderer, E e) {
		renderer.setColor(Color.WHITE);
		renderer.circle(e.getPosition().getX(), e.getPosition().getY(), e.getCollisionRadius(), 16);
		renderer.line(e.getPosition().getX(), e.getPosition().getY(), e.getPosition().getX() + (float) Math.cos(e.getMovingAngle()), e.getPosition().getY() + (float) Math.sin(e.getMovingAngle()));
	}
}
