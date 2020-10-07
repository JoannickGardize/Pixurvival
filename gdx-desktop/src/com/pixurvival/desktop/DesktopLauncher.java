package com.pixurvival.desktop;

import java.io.FileNotFoundException;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.pixurvival.core.util.ArgsUtils;
import com.pixurvival.core.util.ReleaseVersion;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.util.ClientMainArgs;

public class DesktopLauncher {

	public static void main(String[] args) throws FileNotFoundException {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.foregroundFPS = 60;
		config.width = 1600;
		config.height = 900;
		config.title = "Pixurvival - " + ReleaseVersion.getActual().displayName();
		config.addIcon("icon.png", FileType.Internal);
		config.forceExit = false;
		new LwjglApplication(new PixurvivalGame(ArgsUtils.readArgs(args, ClientMainArgs.class)), config);
	}
}
