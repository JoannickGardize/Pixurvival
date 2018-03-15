package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.PlayerEntity;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.message.Direction;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.graphics.TextureAnimation;
import com.pixurvival.gdxcore.graphics.TextureAnimationSet;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerDrawer implements EntityDrawer<PlayerEntity> {

	private @NonNull TextureAnimationSet textureAnimationSet;

	@Override
	public void update(PlayerEntity e) {
		updateDrawPosition(e);
	}

	@Override
	public void draw(Batch batch, PlayerEntity e) {
		TextureAnimation textureAnimation = getAnimation(e);
		int index = getIndexAndUpdateTimer(e, textureAnimation);
		Texture texture = textureAnimation.getTexture(index);
		DrawData data = (DrawData) e.getCustomData();
		Vector2 drawPosition = data.getDrawPosition();
		float x = (float) (drawPosition.x - textureAnimationSet.getWidth() / 2);
		float y = (float) (drawPosition.y - e.getBoundingRadius()) + textureAnimationSet.getYOffset();
		batch.draw(textureAnimationSet.getShadow(), x, y - textureAnimationSet.getWidth() / 4,
				textureAnimationSet.getWidth(), textureAnimationSet.getWidth() / 2);

		if (e.getWorld().getMap().tileAt(drawPosition).getTileDefinition().getVelocityFactor() < 1) {
			batch.draw(texture, x, y, textureAnimationSet.getWidth(), (float) (textureAnimationSet.getHeight() * 0.7),
					0, 0.7f, 1, 0);
		} else {
			batch.draw(texture, x, y, textureAnimationSet.getWidth(), textureAnimationSet.getHeight());
		}
	}

	private int getIndexAndUpdateTimer(PlayerEntity e, TextureAnimation textureAnimation) {
		Object o = e.getCustomData();
		if (o == null) {
			o = new DrawData();
			((DrawData) o).getDrawPosition().set(e.getPosition());
			e.setCustomData(o);
		}

		DrawData data = (DrawData) o;
		float timer = data.getTimer();
		timer += Gdx.graphics.getRawDeltaTime();
		while (timer >= textureAnimation.getFrameDuration() * textureAnimation.size()) {
			timer -= textureAnimation.getFrameDuration() * textureAnimation.size();
		}
		data.setTimer(timer);
		return (int) (timer / textureAnimation.getFrameDuration());
	}

	private TextureAnimation getAnimation(PlayerEntity e) {
		Direction aimingDirection = Direction.closestCardinal(e.getMovingAngle());
		if (e.isForward()) {
			return textureAnimationSet.get(ActionAnimation.getMoveFromDirection(aimingDirection));
		} else {
			return textureAnimationSet.get(ActionAnimation.getStandFromDirection(aimingDirection));
		}
	}

	private void updateDrawPosition(PlayerEntity e) {
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
