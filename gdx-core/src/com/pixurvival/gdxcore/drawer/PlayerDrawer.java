package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.PlayerEntity;
import com.pixurvival.core.contentPack.ActionAnimation;
import com.pixurvival.core.message.Direction;
import com.pixurvival.gdxcore.graphics.TextureAnimation;
import com.pixurvival.gdxcore.graphics.TextureAnimationSet;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerDrawer implements EntityDrawer<PlayerEntity> {

	private @NonNull TextureAnimationSet textureAnimationSet;

	@Override
	public void draw(Batch batch, PlayerEntity e) {
		TextureAnimation textureAnimation = getAnimation(e);

		int index = getIndexAndUpdateTimer(e, textureAnimation);
		Texture texture = textureAnimation.getTexture(index);
		batch.draw(texture, (float) (e.getPosition().x - textureAnimationSet.getXOffset()),
				(float) (e.getPosition().y - textureAnimationSet.getYOffset()), textureAnimationSet.getWidth(),
				textureAnimationSet.getHeight());
	}

	private int getIndexAndUpdateTimer(PlayerEntity e, TextureAnimation textureAnimation) {
		Object o = e.getCustomData();
		if (o == null) {
			o = new AnimationData();
			e.setCustomData(o);
		}
		AnimationData data = (AnimationData) o;
		float timer = data.getTimer();
		timer += Gdx.graphics.getDeltaTime();
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
}
