package com.pixurvival.gdxcore.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Scene2dUtils {

	public static void positionToCenter(Actor actor) {
		Viewport viewport = actor.getStage().getViewport();
		actor.setPosition((viewport.getWorldWidth() - actor.getWidth()) / 2, (viewport.getWorldHeight() - actor.getHeight()) / 2);
	}
}
