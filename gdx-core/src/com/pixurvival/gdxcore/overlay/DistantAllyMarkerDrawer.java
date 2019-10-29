package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.drawer.DrawData;
import com.pixurvival.gdxcore.util.Caches;

public class DistantAllyMarkerDrawer implements OverlayDrawer<PlayerEntity> {

	private Texture arrow;

	private Vector2 tmp = new Vector2();
	private GlyphLayout distanceText = new GlyphLayout();

	public DistantAllyMarkerDrawer() {
		arrow = PixurvivalGame.getInstance().getAssetManager().get(PixurvivalGame.ARROW, Texture.class);
	}

	@Override
	public void draw(Batch batch, Viewport worldViewport, PlayerEntity e) {
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		com.pixurvival.core.util.Vector2 myPosition = null;
		DrawData drawData = ((DrawData) myPlayer.getCustomData());
		if (drawData != null) {
			myPosition = drawData.getDrawPosition();
		} else {
			myPosition = myPlayer.getPosition();
		}
		float px = (float) myPosition.getX();
		float py = (float) myPosition.getY();
		com.pixurvival.core.util.Vector2 otherPosition = e.getPosition();
		Vector2 intersection = findEdgePoint(worldViewport, px, py, (float) otherPosition.getX(), (float) otherPosition.getY());
		if (intersection == null) {
			return;
		}
		if (myPlayer.getWorld().getEntityPool().get(EntityGroup.PLAYER, e.getId()) == null) {
			e.getPosition().addX(e.getVelocity().getX() * Gdx.graphics.getDeltaTime());
			e.getPosition().addY(e.getVelocity().getY() * Gdx.graphics.getDeltaTime());
		}
		Vector2 drawPosition = worldViewport.project(intersection);
		tmp.set(px, py);
		Vector2 screenPlayer = worldViewport.project(tmp);
		float angleToPlayer = screenPlayer.sub(drawPosition).angleRad();
		tmp.set(MathUtils.cos(angleToPlayer), MathUtils.sin(angleToPlayer));
		drawPosition.mulAdd(tmp, 40);
		Color originalColor = batch.getColor();
		Color green = new Color(Color.GREEN);
		batch.setColor(green.mul(originalColor));
		float w2 = arrow.getWidth() / 2f;
		float h2 = arrow.getHeight() / 2f;
		batch.draw(arrow, drawPosition.x - w2, drawPosition.y - h2, w2, h2, arrow.getWidth(), arrow.getHeight(), 1, 1, angleToPlayer * MathUtils.radiansToDegrees + 180, 0, 0, arrow.getWidth(),
				arrow.getHeight(), false, false);
		batch.setColor(originalColor);
		GlyphLayout nameText = Caches.overlayGlyphLayout.get(e.getName());
		drawPosition.mulAdd(tmp, 50);
		BitmapFont font = PixurvivalGame.getOverlayFont();
		font.draw(batch, nameText, drawPosition.x - nameText.width / 2, drawPosition.y + font.getLineHeight() / 2);
		distanceText.setText(font, "(" + (int) myPosition.distance(otherPosition) + ")");
		font.draw(batch, distanceText, drawPosition.x - distanceText.width / 2, drawPosition.y - font.getLineHeight() / 2);
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
