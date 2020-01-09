package com.pixurvival.gdxcore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.client.ClientGameListener;
import com.pixurvival.client.PixurvivalClient;
import com.pixurvival.core.EndGameData;
import com.pixurvival.core.SoundPreset;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.serialization.ContentPackValidityCheckResult;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.lobby.LobbyMessage;
import com.pixurvival.gdxcore.drawer.DrawData;
import com.pixurvival.gdxcore.input.InputMapping;
import com.pixurvival.gdxcore.lobby.MultiplayerLobbyScreen;
import com.pixurvival.gdxcore.lobby.SingleplayerLobbyScreen;
import com.pixurvival.gdxcore.menu.MainMenuScreen;
import com.pixurvival.gdxcore.textures.ContentPackTextures;
import com.pixurvival.gdxcore.util.ClientMainArgs;

import lombok.Getter;

public class PixurvivalGame extends Game implements ClientGameListener {

	public static final String I18N_BUNDLE = "i18n/Bundle";
	public static final String SKIN = "kenney-pixel/skin/skin.json";
	public static final String DEFAULT_FONT = "default_font.ttf";
	public static final String DEFAULT_ITALIC_FONT = "default_italic_font.ttf";
	public static final String OVERLAY_FONT = "overlay_font.ttf";
	public static final String ARROW = "arrow.png";

	public static Skin getSkin() {
		return instance.skin;
	}

	public static PixurvivalClient getClient() {
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

	public static BitmapFont getDefaultItalicFont() {
		return instance.assetManager.get(DEFAULT_ITALIC_FONT, BitmapFont.class);
	}

	public static ContentPackTextures getContentPackTextures() {
		return instance.contentPackTextures;
	}

	public static World getWorld() {
		return instance.client.getWorld();
	}

	private static @Getter PixurvivalGame instance = null;

	private Map<Class<? extends Screen>, Screen> screens = new HashMap<>();
	private PixurvivalClient client;
	private @Getter AssetManager assetManager;
	private @Getter InputMapping keyMapping;
	private float frameDurationMillis = 1000f / 30;
	private float frameCounter;
	private float interpolationTime = 0;
	private ContentPackTextures contentPackTextures;
	private Skin skin;
	private @Getter boolean zoomEnabled;
	private Sound[] sounds;
	private @Getter float globalVolume = 1;

	public PixurvivalGame(ClientMainArgs clientArgs) {
		if (instance != null) {
			throw new IllegalStateException("Cannot instantiate multiple instances of the game !");
		}
		if (clientArgs.isMute()) {
			globalVolume = 0;
		}
		zoomEnabled = clientArgs.isZoomEnabled();
		instance = this;
		client = new PixurvivalClient(clientArgs);
		client.addListener(this);
		if (clientArgs.isRedirectErrorToFile()) {
			File file = new File("err.txt");
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(file);
				PrintStream ps = new PrintStream(fos);
				System.setErr(ps);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public Sound getSound(SoundPreset sound) {
		return sounds[sound.ordinal()];
	}

	@Override
	public void create() {
		assetManager = new AssetManager();
		loadFonts();
		loadSounds();
		assetManager.load(I18N_BUNDLE, I18NBundle.class);
		assetManager.load(ARROW, Texture.class);
		// TODO barre de chargement
		assetManager.finishLoading();
		sounds = new Sound[SoundPreset.values().length];
		for (int i = 0; i < sounds.length; i++) {
			sounds[i] = assetManager.get(getSoundFileName(SoundPreset.values()[i]));
		}
		getDefaultFont().getData().markupEnabled = true;
		skin = new Skin();
		skin.add("default", getDefaultFont(), BitmapFont.class);
		skin.add("default-italic", getDefaultItalicFont(), BitmapFont.class);
		skin.addRegions(new TextureAtlas(Gdx.files.internal("kenney-pixel/skin/skin.atlas")));
		skin.load(Gdx.files.internal(SKIN));
		setScreen(MainMenuScreen.class);
	}

	private void loadFonts() {
		assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(assetManager.getFileHandleResolver()));
		assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(assetManager.getFileHandleResolver()));

		FreeTypeFontLoaderParameter defaultFontParams = new FreeTypeFontLoaderParameter();
		defaultFontParams.fontFileName = "OpenSans-Bold.ttf";
		defaultFontParams.fontParameters.size = (int) (Gdx.graphics.getDensity() * 25);

		assetManager.load(DEFAULT_FONT, BitmapFont.class, defaultFontParams);

		FreeTypeFontLoaderParameter defaultItalicFontParams = new FreeTypeFontLoaderParameter();
		defaultItalicFontParams.fontFileName = "OpenSans-Italic.ttf";
		defaultItalicFontParams.fontParameters.size = (int) (Gdx.graphics.getDensity() * 25);
		assetManager.load(DEFAULT_ITALIC_FONT, BitmapFont.class, defaultItalicFontParams);

		FreeTypeFontLoaderParameter overlayFontParams = new FreeTypeFontLoaderParameter();
		overlayFontParams.fontFileName = "OpenSans-Bold.ttf";
		overlayFontParams.fontParameters.size = (int) (Gdx.graphics.getDensity() * 20);
		overlayFontParams.fontParameters.borderColor = Color.BLACK;
		overlayFontParams.fontParameters.borderWidth = 1;
		assetManager.load(OVERLAY_FONT, BitmapFont.class, overlayFontParams);
	}

	private void loadSounds() {
		for (SoundPreset soundEnum : SoundPreset.values()) {
			assetManager.load(getSoundFileName(soundEnum), Sound.class);
		}
	}

	private String getSoundFileName(SoundPreset soundEnum) {
		return "sounds/" + soundEnum.name().toLowerCase() + ".wav";
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
				return k.newInstance();
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
			Log.info("Loading texture with pixel width : " + pixelWidth);
			contentPackTextures.load(client.getWorld().getContentPack(), pixelWidth);
		} catch (ContentPackException e) {
			Log.error("Error when loading contentPack.", e);
		}
		worldScreen.setWorld(client.getWorld());
		setScreen(worldScreen);
		if (client.getWorld().getType() == World.Type.CLIENT) {
			client.sendGameReady();
		}
	}

	@Override
	public void error(Throwable e) {
	}

	@Override
	public void spectatorStarted() {
		DrawData drawData = (DrawData) getClient().getMyPlayer().getCustomData();
		if (drawData != null) {
			drawData.getDrawPosition().set(getClient().getMyPlayer().getPosition());
		}
	}

	@Override
	public void gameEnded(EndGameData data) {
		Screen screen = getScreen();
		if (screen instanceof WorldScreen) {
			((WorldScreen) screen).showEndGame(data);
		}
	}

	@Override
	public void enterLobby() {
		setScreen(MultiplayerLobbyScreen.class);
	}

	@Override
	public void lobbyMessageReceived(LobbyMessage message) {
		if (getScreen() instanceof MultiplayerLobbyScreen) {
			((MultiplayerLobbyScreen) getScreen()).receivedLobbyMessage(message);
		} else if (getScreen() instanceof SingleplayerLobbyScreen) {
			((SingleplayerLobbyScreen) getScreen()).received(message);
		}
	}

	@Override
	public void contentPackAvailable(ContentPackIdentifier identifier) {

	}

	@Override
	public void questionDownloadContentPack(ContentPackIdentifier identifier, ContentPackValidityCheckResult checkResult) {
		if (getScreen() instanceof MultiplayerLobbyScreen) {
			((MultiplayerLobbyScreen) getScreen()).questionDownloadContentPack(identifier, checkResult);
		}
	}

	@Override
	public void gameStarted() {
		Screen screen = getScreen();
		if (screen instanceof WorldScreen) {
			((WorldScreen) screen).gameStarted();
		}
	}
}
