package com.pixurvival.gdxcore;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.client.ClientGame;

import lombok.Getter;

public class PixurvivalGame extends Game {

	private Map<Class<? extends Screen>, Screen> screens = new HashMap<>();
	private ClientGame clientGame;
	private @Getter AssetManager assetManager;

	@Override
	public void create() {
		clientGame = new ClientGame();
		assetManager = new AssetManager();
		assetManager.load(Assets.I18N_BUNDLE, I18NBundle.class);
		assetManager.load(Assets.SKIN, Skin.class);
		// TODO barre de chargement
		assetManager.finishLoading();
		setScreen(MainMenuScreen.class);
	}

	public void setScreen(Class<? extends Screen> screenClass) {
		Screen screen = screens.get(screens);
		if (screen == null) {
			try {
				screen = screenClass.getConstructor(PixurvivalGame.class).newInstance(this);
				screens.put(screenClass, screen);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				Log.error("Error when trying to instantiate new Screen", e);
			}
		}
		setScreen(screen);
	}

	public String getString(String key) {
		return assetManager.get(Assets.I18N_BUNDLE, I18NBundle.class).get(key);
	}

	@Override
	public void dispose() {
		assetManager.dispose();
		super.dispose();
	}
}
