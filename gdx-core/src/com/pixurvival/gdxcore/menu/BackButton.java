package com.pixurvival.gdxcore.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pixurvival.gdxcore.PixurvivalGame;

public class BackButton extends TextButton {

	public BackButton() {
		super(PixurvivalGame.getString("generic.back"), PixurvivalGame.getSkin());
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				PixurvivalGame.setScreen(MainMenuScreen.class);
			}
		});
	}

}
