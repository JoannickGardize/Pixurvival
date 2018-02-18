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
import com.pixurvival.client.ClientGameListener;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.gdxcore.menu.MainMenuScreen;

import lombok.Getter;

public class PixurvivalGame extends Game implements ClientGameListener {

	private Map<Class<? extends Screen>, Screen> screens = new HashMap<>();
	private @Getter ClientGame client;
	private @Getter AssetManager assetManager;
	private @Getter KeyMapping KeyMapping;

	@Override
	public void create() {
		client = new ClientGame();
		client.addListener(this);
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

	@Override
	public void loginResponse(LoginResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startGame() {
		// TODO Auto-generated method stub

	}

	@Override
	public void error(Throwable e) {
		// TODO Auto-generated method stub

	}
}
