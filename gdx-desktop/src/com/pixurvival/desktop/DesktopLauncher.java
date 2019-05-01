package com.pixurvival.desktop;

import java.lang.reflect.InvocationTargetException;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.pixurvival.core.util.ArgsUtils;
import com.pixurvival.core.util.CommonMainArgs;
import com.pixurvival.gdxcore.PixurvivalGame;

public class DesktopLauncher {

	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.foregroundFPS = 60;
		config.width = 1600;
		config.height = 900;
		// DisplayMode desktopMode =
		// LwjglApplicationConfiguration.getDesktopDisplayMode();
		// config.setFromDisplayMode(desktopMode);
		new LwjglApplication(new PixurvivalGame(ArgsUtils.readArgs(args, CommonMainArgs.class)), config);
	}
}
