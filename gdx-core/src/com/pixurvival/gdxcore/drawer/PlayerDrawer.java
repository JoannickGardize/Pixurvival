package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.PlayerEntity;
import com.pixurvival.gdxcore.graphics.TextureAnimation;
import com.pixurvival.gdxcore.graphics.TextureAnimationSet;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerDrawer implements EntityDrawer<PlayerEntity> {

	private @NonNull TextureAnimationSet textureAnimationSet;

	@Override
	public void draw(Batch batch, PlayerEntity e) {
		float timer = getAndUpdateTimer(e);
		int index = (int) (timer / textureAnimation.getFrameDuration());
		while (index >= textureAnimation.size()) {
			timer -= textureAnimation.getFrameDuration() * textureAnimation.size();
		}
		data.setTimer(timer);
	}

	private float getAndUpdateTimer(PlayerEntity e) {
		Object o = e.getCustomData();
		if (o == null) {
			o = new AnimationData();
			e.setCustomData(o);
		}
		AnimationData data = (AnimationData) o;
		float timer = data.getTimer();
		timer += Gdx.graphics.getDeltaTime();
		return timer;
	}

	private TextureAnimation getAnimation(PlayerEntity e) {
		if()
	}
}
