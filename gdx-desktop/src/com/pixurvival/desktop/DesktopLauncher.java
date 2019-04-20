package com.pixurvival.desktop;

import java.lang.reflect.InvocationTargetException;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.gdxcore.PixurvivalGame;

public class DesktopLauncher {

	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		if (args.length > 0) {
			Log.class.getMethod(args[0]).invoke(null);
		}

		// if (arg.length > 0 && arg[0] == "editor") {
		// new ContentPackEditor().setVisible(true);
		// } else {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.foregroundFPS = 60;
		// DisplayMode desktopMode =
		// LwjglApplicationConfiguration.getDesktopDisplayMode();
		// config.setFromDisplayMode(desktopMode);
		new LwjglApplication(new PixurvivalGame(), config);
		// }
	}
}
