package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class UIWindow extends Window {

	public UIWindow(String title, Skin skin) {
		super(title, skin);
		getCaptureListeners().clear();
		setResizable(true);
	}

}
