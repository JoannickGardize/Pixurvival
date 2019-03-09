package com.pixurvival.gdxcore.util;

import com.badlogic.gdx.Gdx;
import com.pixurvival.core.CustomDataHolder;
import com.pixurvival.core.Entity;
import com.pixurvival.gdxcore.drawer.DrawData;
import com.pixurvival.gdxcore.textures.TextureAnimation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GraphicsUtil {

	public static int getIndexAndUpdateTimer(CustomDataHolder e, TextureAnimation textureAnimation) {
		DrawData o = (DrawData) e.getCustomData();
		if (o == null) {
			o = new DrawData();
			e.setCustomData(o);
			if (e instanceof Entity) {
				o.getDrawPosition().set(((Entity) e).getPosition());
			}
		}
		DrawData data = o;
		float timer = data.getTimer();
		timer += Gdx.graphics.getRawDeltaTime();
		while (timer >= textureAnimation.getFrameDuration() * textureAnimation.size()) {
			timer -= textureAnimation.getFrameDuration() * textureAnimation.size();
		}
		data.setTimer(timer);
		return (int) (timer / textureAnimation.getFrameDuration());
	}

}
