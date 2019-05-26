package com.pixurvival.gdxcore;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
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
	public static final String DEFAULT_FONT = "default_font.ttf";
	public static final String OVERLAY_FONT = "overlay_font.ttf";

	public static Skin getSkin() {
		return instance.skin;
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

	public static BitmapFont getOverlayFont() {
		return instance.assetManager.get(OVERLAY_FONT, BitmapFont.class);
	}

	public static BitmapFont getDefaultFont() {
		return instance.assetManager.get(DEFAULT_FONT, BitmapFont.class);
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
	private Skin skin;

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
		assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(assetManager.getFileHandleResolver()));
		assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(assetManager.getFileHandleResolver()));

		FreeTypeFontLoaderParameter defaultFontParams = new FreeTypeFontLoaderParameter();
		defaultFontParams.fontFileName = "OpenSans-Bold.ttf";
		defaultFontParams.fontParameters.size = (int) (Gdx.graphics.getDensity() * 25);
		// defaultFontParams.fontParameters.genMipMaps = true;
		// defaultFontParams.fontParameters.minFilter =
		// TextureFilter.MipMapNearestNearest;
		// defaultFontParams.fontParameters.magFilter =
		// TextureFilter.MipMapNearestNearest;

		assetManager.load(DEFAULT_FONT, BitmapFont.class, defaultFontParams);

		FreeTypeFontLoaderParameter overlayFontParams = new FreeTypeFontLoaderParameter();
		overlayFontParams.fontFileName = "OpenSans-Bold.ttf";
		overlayFontParams.fontParameters.size = (int) (Gdx.graphics.getDensity() * 20);
		overlayFontParams.fontParameters.borderColor = Color.BLACK;
		overlayFontParams.fontParameters.borderWidth = 1;
		assetManager.load(OVERLAY_FONT, BitmapFont.class, overlayFontParams);

		assetManager.load(I18N_BUNDLE, I18NBundle.class);
		// TODO barre de chargement
		assetManager.finishLoading();
		getDefaultFont().getData().markupEnabled = true;
		skin = new Skin();
		skin.add("default", getDefaultFont(), BitmapFont.class);
		skin.addRegions(new TextureAtlas(Gdx.files.internal("kenney-pixel/skin/skin.atlas")));
		skin.load(Gdx.files.internal(SKIN));

		setScreen(MainMenuScreen.class);
	}

	@Override
	public void render() {
		frameCounter += Gdx.graphics.getDeltaTime() * 1000;
		interpolationTime += Gdx.graphics.getDeltaTime();
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
	}

	@Override
	public void initializeGame() {
		WorldScreen worldScreen = new WorldScreen();
		contentPackTextures = new ContentPackTextures();
		try {
			// int screenWidth = Math.min(Gdx.graphics.getWidth(),
			// Gdx.graphics.getHeight());
			// int pixelWidth = Math.round(screenWidth /
			// (WorldScreen.VIEWPORT_WORLD_WIDTH *
			// GameConstants.PIXEL_PER_UNIT));
			// Seems better :
			int pixelWidth = 3;
			Log.info("Loading texture with optimal pixel width : " + pixelWidth);
			contentPackTextures.load(client.getWorld().getContentPack(), pixelWidth);
		} catch (ContentPackException e) {
			Log.error("Error when loading contentPack.", e);
			e.printStackTrace();
		}
		worldScreen.setWorld(client.getWorld());
		setScreen(worldScreen);
		if (client.getWorld().getType() == World.Type.CLIENT) {
			client.notifyReady();
		}
	}

	@Override
	public void error(Throwable e) {
	}
}
