package com.pixurvival.gdxcore.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.Viewport;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Scene2dUtils {

	public static void positionToCenter(Actor actor) {
		Viewport viewport = actor.getStage().getViewport();
		actor.setPosition((viewport.getWorldWidth() - actor.getWidth()) / 2, (viewport.getWorldHeight() - actor.getHeight()) / 2);
	}

	public static Action yellowLightning() {
		return Actions.delay(0.1f, Actions.repeat(2, Actions.sequence(Actions.color(Color.YELLOW, 0.25f, Interpolation.circle), Actions.color(Color.WHITE, 0.25f, Interpolation.circle))));
	}
}
