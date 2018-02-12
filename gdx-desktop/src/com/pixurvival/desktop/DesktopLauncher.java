package com.pixurvival.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.pixurvival.gdxcore.OldPixurvivalGame;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		// DisplayMode desktopMode =
		// LwjglApplicationConfiguration.getDesktopDisplayMode();
		// config.setFromDisplayMode(desktopMode);
		new LwjglApplication(new OldPixurvivalGame(), config);
	}
}
