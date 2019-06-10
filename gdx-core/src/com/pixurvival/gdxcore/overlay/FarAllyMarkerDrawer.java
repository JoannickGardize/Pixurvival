package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.gdxcore.PixurvivalGame;

public class FarAllyMarkerDrawer implements OverlayDrawer<PlayerEntity> {

	private Texture arrow;

	private Vector2 tmp = new Vector2();

	public FarAllyMarkerDrawer() {
		arrow = PixurvivalGame.getInstance().getAssetManager().get(PixurvivalGame.ARROW, Texture.class);

	}

	@Override
	public void draw(Batch batch, Viewport worldViewport, PlayerEntity e) {
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		float px = (float) myPlayer.getPosition().getX();
		float py = (float) myPlayer.getPosition().getY();
		Vector2 intersection = findEdgePoint(worldViewport, px, py, (float) e.getPosition().getX(), (float) e.getPosition().getY());
		if (intersection == null) {
			return;
		}
		Vector2 screenIntersection = worldViewport.project(intersection);
		tmp.set(px, py);
		Vector2 screenPlayer = worldViewport.project(tmp);
		float angleToPlayer = screenPlayer.sub(screenIntersection).angleRad();
		screenIntersection.add(MathUtils.cos(angleToPlayer) * 40, MathUtils.sin(angleToPlayer) * 40);
		Color originalColor = batch.getColor();
		Color green = new Color(Color.GREEN);
		batch.setColor(green.mul(originalColor));
		batch.draw(arrow, screenIntersection.x - arrow.getWidth() / 2f, screenIntersection.y - arrow.getHeight() / 2f, arrow.getWidth() / 2f, arrow.getHeight() / 2f, arrow.getWidth(),
				arrow.getHeight(), 1, 1, angleToPlayer * MathUtils.radiansToDegrees + 180, 0, 0, arrow.getWidth(), arrow.getHeight(), false, false);
		batch.setColor(originalColor);
	}

	private Vector2 findEdgePoint(Viewport worldViewport, float x1, float y1, float x2, float y2) {
		Vector2 intersection = new Vector2();

		float camX = worldViewport.getCamera().position.x;
		float camY = worldViewport.getCamera().position.y;
		float width = worldViewport.getWorldWidth() / 2;
		float height = worldViewport.getWorldHeight() / 2;
		if (Intersector.intersectSegments(x1, y1, x2, y2, camX - width, camY + height, camX + width, camY + height, intersection)) {
			return intersection;
		} else if (Intersector.intersectSegments(x1, y1, x2, y2, camX - width, camY - height, camX + width, camY - height, intersection)) {
			return intersection;
		} else if (Intersector.intersectSegments(x1, y1, x2, y2, camX - width, camY - height, camX - width, camY + height, intersection)) {
			return intersection;
		} else if (Intersector.intersectSegments(x1, y1, x2, y2, camX + width, camY - height, camX + width, camY + height, intersection)) {
			return intersection;
		} else {
			return null;
		}
	}
}
