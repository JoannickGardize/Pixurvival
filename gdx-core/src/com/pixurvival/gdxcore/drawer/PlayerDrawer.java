package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.PlayerEntity;
import com.pixurvival.gdxcore.graphics.TextureAnimation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerDrawer implements EntityDrawer<PlayerEntity> {

	private @NonNull TextureAnimation textureAnimation;

	@Override
	public void draw(Batch batch, PlayerEntity e) {
		Object o = e.getCustomData();
		if (o == null) {
			o = new AnimationData();
			e.setCustomData(o);
		}
		AnimationData data = (AnimationData) o;
		float timer = data.getTimer();
		timer += Gdx.graphics.getDeltaTime();
		int index = (int) (timer / textureAnimation.getFrameDuration());
		while (index >= textureAnimation.size()) {
			timer -= textureAnimation.getFrameDuration() * textureAnimation.size();
		}
		data.setTimer(timer);
	}

}
