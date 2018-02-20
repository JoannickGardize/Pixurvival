package com.pixurvival.gdxcore;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.client.ClientGame;
import com.pixurvival.client.ClientGameListener;
import com.pixurvival.core.contentPack.ContentPackReadException;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.gdxcore.graphics.ContentPackTextureAnimations;
import com.pixurvival.gdxcore.menu.MainMenuScreen;

import lombok.Getter;

public class PixurvivalGame extends Game implements ClientGameListener {

	private Map<Class<? extends Screen>, Screen> screens = new HashMap<>();
	private @Getter ClientGame client;
	private @Getter AssetManager assetManager;
	private @Getter KeyMapping KeyMapping;
	private double frameDurationMillis = 1000.0 / 30;
	private double frameCounter;

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

	@Override
	public void render() {
		frameCounter += Gdx.graphics.getDeltaTime() * 1000;
		while (frameCounter >= frameDurationMillis) {
			client.update(frameDurationMillis);
			frameCounter -= frameDurationMillis;
		}
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}

	public void setScreen(Class<? extends Screen> screenClass) {
		Screen screen = screens.get(screenClass);
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
		WorldScreen worldScreen = new WorldScreen(this);
		ContentPackTextureAnimations contentPackTextureAnimations = new ContentPackTextureAnimations();
		try {
			contentPackTextureAnimations.load(client.getWorld().getContentPack(), 10);
		} catch (ContentPackReadException e) {
			e.printStackTrace();
		}
		worldScreen.setWorld(client.getWorld(), contentPackTextureAnimations, client.getMyPlayerId());
		setScreen(worldScreen);
		client.notifyReady();
	}

	@Override
	public void error(Throwable e) {
		// TODO Auto-generated method stub

	}
}
