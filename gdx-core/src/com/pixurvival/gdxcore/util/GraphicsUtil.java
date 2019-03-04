package com.pixurvival.gdxcore.util;

import com.badlogic.gdx.Gdx;
import com.pixurvival.core.CustomDataHolder;
import com.pixurvival.gdxcore.drawer.DrawData;
import com.pixurvival.gdxcore.textures.TextureAnimation;

public class GraphicsUtil {

	public int getIndexAndUpdateTimer(CustomDataHolder e, TextureAnimation textureAnimation) {
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

}
