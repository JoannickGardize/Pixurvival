package com.pixurvival.gdxcore.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pixurvival.gdxcore.PixurvivalGame;

public class MenuButton extends TextButton {
	public MenuButton(String textKey, Runnable action) {
		super(PixurvivalGame.getString(textKey), PixurvivalGame.getSkin());

		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				action.run();
			}
		});
	}

}
