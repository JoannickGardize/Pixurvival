package com.pixurvival.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.pixurvival.core.util.ArgsUtils;
import com.pixurvival.core.util.ReleaseVersion;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.util.ClientMainArgs;

public class DesktopLauncher {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setWindowedMode(1600, 900);
        config.setTitle("Pixurvival - " + ReleaseVersion.actual().displayName());
        config.setWindowIcon(FileType.Internal, "icon.png");
        new Lwjgl3Application(new PixurvivalGame(ArgsUtils.readArgs(args, ClientMainArgs.class)), config);
    }
}
