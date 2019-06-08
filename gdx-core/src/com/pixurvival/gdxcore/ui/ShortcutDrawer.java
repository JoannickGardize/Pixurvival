package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.input.InputAction;
import com.pixurvival.gdxcore.input.InputManager;
import com.pixurvival.gdxcore.util.Caches;

public class ShortcutDrawer {

	public static final int BOTTOM = 0;
	public static final int TOP = 1;

	private static final int PADDING = 5;

	private Actor actor;
	private int position;
	private GlyphLayout text;

	public ShortcutDrawer(Actor actor, InputAction action, int position) {
		this.actor = actor;
		this.position = position;
		String s = InputManager.getInstance().getMapping().getButton(action).toString();
		text = Caches.overlayYellowGlyphLayout.get(s);
	}

	public void draw(Batch batch) {
		float y;
		if (position == BOTTOM) {
			y = PADDING + text.height;
		} else {
			y = actor.getHeight() - PADDING;
		}
		PixurvivalGame.getOverlayFont().draw(batch, text, actor.getX() + 5, actor.getY() + y);
	}
}
