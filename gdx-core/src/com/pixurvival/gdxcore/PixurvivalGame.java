package com.pixurvival.gdxcore;

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
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.util.CommonMainArgs;
import com.pixurvival.gdxcore.menu.MainMenuScreen;
import com.pixurvival.gdxcore.textures.ContentPackTextures;

import lombok.Getter;

public class PixurvivalGame extends Game implements ClientGameListener {

	public static final String I18N_BUNDLE = "i18n/Bundle";
	public static final String SKIN = "kenney-pixel/skin/skin.json";

	public static Skin getSkin() {
		return instance.assetManager.get(SKIN, Skin.class);
	}

	public static ClientGame getClient() {
		return instance.client;
	}

	public static void setScreen(Class<? extends Screen> screenClass) {
		instance.setScreenInternal(screenClass);
	}

	public static float getInterpolationTime() {
		return instance.interpolationTime;
	}

	public static String getString(String key) {
		return instance.assetManager.get(I18N_BUNDLE, I18NBundle.class).get(key);
	}

	public static ContentPackTextures getContentPackTextures() {
		return instance.contentPackTextures;
	}

	public static World getWorld() {
		return instance.client.getWorld();
	}

	private static @Getter PixurvivalGame instance = null;

	private Map<Class<? extends Screen>, Screen> screens = new HashMap<>();
	private ClientGame client;
	private AssetManager assetManager;
	private @Getter KeyMapping keyMapping;
	private double frameDurationMillis = 1000.0 / 30;
	private double frameCounter;
	private float interpolationTime = 0;
	private ContentPackTextures contentPackTextures;

	public PixurvivalGame(CommonMainArgs clientArgs) {
		if (instance != null) {
			throw new IllegalStateException("Cannot instantiate multiple instances of the game !");
		}
		instance = this;
		client = new ClientGame(clientArgs);
		client.addListener(this);
	}

	@Override
	public void create() {
		assetManager = new AssetManager();
		assetManager.load(I18N_BUNDLE, I18NBundle.class);
		assetManager.load(SKIN, Skin.class);
		// TODO barre de chargement
		assetManager.finishLoading();

		setScreen(MainMenuScreen.class);
	}

	@Override
	public void render() {
		frameCounter += Gdx.graphics.getRawDeltaTime() * 1000;
		interpolationTime += Gdx.graphics.getRawDeltaTime();
		while (frameCounter >= frameDurationMillis) {
			client.update(frameDurationMillis);
			frameCounter -= frameDurationMillis;
			interpolationTime = 0;
		}
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}

	private void setScreenInternal(Class<? extends Screen> screenClass) {
		Screen screen = screens.computeIfAbsent(screenClass, k -> {
			try {
				return (Screen) k.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
				Log.error("Error when trying to instantiate new Screen", e);
				return null;
			}
		});
		setScreen(screen);
	}

	@Override
	public void dispose() {
		assetManager.dispose();
		if (client.getWorld() != null) {
			client.getWorld().unload();
		}
		super.dispose();
	}

	@Override
	public void loginResponse(LoginResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initializeGame() {
		WorldScreen worldScreen = new WorldScreen();
		contentPackTextures = new ContentPackTextures();
		try {
			// int screenWidth = Math.min(Gdx.graphics.getWidth(),
			// Gdx.graphics.getHeight());
			// int pixelWidth = Math
			// .round((float) screenWidth / (WorldScreen.VIEWPORT_WORLD_WIDTH *
			// World.PIXEL_PER_UNIT));
			// Seems better :
			int pixelWidth = 3;
			Log.info("Loading texture with optimal pixel width : " + pixelWidth);
			contentPackTextures.load(client.getWorld().getContentPack(), pixelWidth);
		} catch (ContentPackException e) {
			Log.error("Error when loading contentPack.", e);
			e.printStackTrace();
		}
		worldScreen.setWorld(client.getWorld(), client.getMyPlayerId());
		setScreen(worldScreen);
		if (client.getWorld().getType() == World.Type.CLIENT) {
			client.notifyReady();
		}
	}

	@Override
	public void error(Throwable e) {
		// TODO Auto-generated method stub

	}
}
