package com.pixurvival.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.pixurvival.contentPackEditor.ContentPackEditor;
import com.pixurvival.gdxcore.PixurvivalGame;

public class DesktopLauncher {

	public static void main(String[] arg) {

		if (arg.length > 0 && arg[0] == "editor") {
			new ContentPackEditor().setVisible(true);
		} else {
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			config.foregroundFPS = 60;
			// DisplayMode desktopMode =
			// LwjglApplicationConfiguration.getDesktopDisplayMode();
			// config.setFromDisplayMode(desktopMode);
			new LwjglApplication(new PixurvivalGame(), config);
		}
	}
}
