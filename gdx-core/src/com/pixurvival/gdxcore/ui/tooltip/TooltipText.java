package com.pixurvival.gdxcore.ui.tooltip;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.pixurvival.gdxcore.PixurvivalGame;

public class TooltipText extends Label {

	public TooltipText(CharSequence text) {
		this(text, false);
	}

	public TooltipText(CharSequence text, boolean italic) {
		super(text, PixurvivalGame.getSkin(), italic ? "white-italic" : "white");
		setWrap(true);
	}

	@Override
	public float getPrefWidth() {
		return Gdx.graphics.getWidth() / 4f;
	}
}
