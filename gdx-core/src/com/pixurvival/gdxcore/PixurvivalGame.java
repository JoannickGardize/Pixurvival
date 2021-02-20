package com.pixurvival.gdxcore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
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
import com.pixurvival.core.SoundEffect;
import com.pixurvival.core.SoundPreset;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.serialization.ContentPackValidityCheckResult;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.lobby.LobbyMessage;
import com.pixurvival.gdxcore.drawer.DrawData;
import com.pixurvival.gdxcore.input.InputMapping;
import com.pixurvival.gdxcore.lobby.MultiplayerLobbyScreen;
import com.pixurvival.gdxcore.lobby.NewSingleplayerLobbyScreen;
import com.pixurvival.gdxcore.menu.MainMenuScreen;
import com.pixurvival.gdxcore.notificationpush.NotificationPushManager;
import com.pixurvival.gdxcore.textures.ChunkTileTexturesManager;
import com.pixurvival.gdxcore.textures.ContentPackAssets;
import com.pixurvival.gdxcore.util.ClientMainArgs;
import com.pixurvival.server.PixurvivalServer;
import com.pixurvival.server.util.ServerMainArgs;

import lombok.Getter;

public class PixurvivalGame extends Game implements ClientGameListener {

	private static final String POLISH_CHARS = "\u0104\u0106\u0118\u0141\u0143\u015A\u0179\u017B\u0105\u0107\u0119\u0142\u0144\u015B\u017A\u017C";
	public static final String I18N_BUNDLE = "i18n/Bundle";
	public static final String SKIN = "kenney-pixel/skin/skin.json";
	public static final String DEFAULT_FONT = "default_font.ttf";
	public static final String DEFAULT_ITALIC_FONT = "default_italic_font.ttf";
	public static final String OVERLAY_FONT = "overlay_font.ttf";
	public static final String ARROW = "arrow.png";
	public static final String RIGHT_CLICK_ICON = "right_click_icon.png";
	public static final String FACTORY_ARROW = "factory_arrow.png";
	public static final String FUEL_BAR = "fuel_bar.png";

	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.US));

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

	public static String getString(String key, Object... args) {
		return instance.assetManager.get(I18N_BUNDLE, I18NBundle.class).format(key, args);
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

	public static ContentPackAssets getContentPackTextures() {
		if (instance.worldScreen == null) {
			return null;
		} else {
			return instance.worldScreen.getContentPackTextures();
		}
	}

	public static ChunkTileTexturesManager getChunkTileTexturesManager() {
		if (instance.worldScreen == null) {
			return null;
		}
		return instance.worldScreen.getChunkTileTexturesManager();
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
	private Skin skin;
	private @Getter boolean zoomEnabled;
	private @Getter Sound[] soundPresets;
	private @Getter float globalVolume = 1;
	private PixurvivalServer server;
	private FileOutputStream errorOutput;
	private boolean skipFrame = false;
	private WorldScreen worldScreen;

	public PixurvivalGame(ClientMainArgs clientArgs) {
		if (instance != null) {
			throw new IllegalStateException("Cannot instantiate multiple instances of the game !");
		}
		if (clientArgs.isMute()) {
			globalVolume = 0;
		}
		if (clientArgs.getLanguage() != null) {
			Locale.setDefault(Locale.forLanguageTag(clientArgs.getLanguage()));
		}

		zoomEnabled = clientArgs.isZoomEnabled();
		instance = this;
		client = new PixurvivalClient(clientArgs);
		client.addListener(this);
		if (clientArgs.isRedirectErrorToFile()) {
			File file = new File("err.txt");

			try {
				errorOutput = new FileOutputStream(file);
				PrintStream ps = new PrintStream(errorOutput);
				System.setErr(ps);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public Sound getSound(SoundEffect sound) {
		ContentPackAssets contentPackAssets = getContentPackTextures();
		if (contentPackAssets != null) {
			return contentPackAssets.getSound(sound.getId());
		} else {
			return null;
		}
	}

	@Override
	public void create() {
		assetManager = new AssetManager();
		loadFonts();
		loadSounds();
		assetManager.load(I18N_BUNDLE, I18NBundle.class);
		assetManager.load(ARROW, Texture.class);
		assetManager.load(RIGHT_CLICK_ICON, Texture.class);
		assetManager.load(FACTORY_ARROW, Texture.class);
		assetManager.load(FUEL_BAR, Texture.class);
		// TODO barre de chargement
		assetManager.finishLoading();

		assetManager.get(FACTORY_ARROW, Texture.class).setFilter(TextureFilter.Linear, TextureFilter.Linear);
		soundPresets = new Sound[SoundPreset.values().length];
		for (int i = 0; i < soundPresets.length; i++) {
			soundPresets[i] = assetManager.get(getSoundFileName(SoundPreset.values()[i]));
		}
		getDefaultFont().getData().markupEnabled = true;
		skin = new Skin();
		skin.add("default", getDefaultFont(), BitmapFont.class);
		skin.add("default-italic", getDefaultItalicFont(), BitmapFont.class);
		skin.addRegions(new TextureAtlas(Gdx.files.internal("kenney-pixel/skin/skin.atlas")));
		skin.load(Gdx.files.internal(SKIN));
		NotificationPushManager.getInstance().start();
		setScreen(MainMenuScreen.class);
	}

	private void loadFonts() {
		assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(assetManager.getFileHandleResolver()));
		assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(assetManager.getFileHandleResolver()));

		FreeTypeFontLoaderParameter defaultFontParams = new FreeTypeFontLoaderParameter();
		defaultFontParams.fontFileName = "OpenSans-Bold.ttf";
		defaultFontParams.fontParameters.size = (int) (Gdx.graphics.getDensity() * 25);
		defaultFontParams.fontParameters.characters += POLISH_CHARS;

		assetManager.load(DEFAULT_FONT, BitmapFont.class, defaultFontParams);

		FreeTypeFontLoaderParameter defaultItalicFontParams = new FreeTypeFontLoaderParameter();
		defaultItalicFontParams.fontFileName = "OpenSans-Italic.ttf";
		defaultItalicFontParams.fontParameters.size = (int) (Gdx.graphics.getDensity() * 25);
		defaultItalicFontParams.fontParameters.characters += POLISH_CHARS;
		assetManager.load(DEFAULT_ITALIC_FONT, BitmapFont.class, defaultItalicFontParams);

		FreeTypeFontLoaderParameter overlayFontParams = new FreeTypeFontLoaderParameter();
		overlayFontParams.fontFileName = "OpenSans-Bold.ttf";
		overlayFontParams.fontParameters.size = (int) (Gdx.graphics.getDensity() * 20);
		overlayFontParams.fontParameters.borderColor = Color.BLACK;
		overlayFontParams.fontParameters.borderWidth = 1;
		overlayFontParams.fontParameters.characters += POLISH_CHARS;
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
		ChunkTileTexturesManager chunkTileTexturesManager = PixurvivalGame.getChunkTileTexturesManager();
		if (chunkTileTexturesManager != null) {
			chunkTileTexturesManager.update(WorldScreen.getWorldStage());
		}
		if (skipFrame) {
			skipFrame = false;
			return;
		}
		try {
			float deltaTime = Gdx.graphics.getRawDeltaTime();
			frameCounter += deltaTime * 1000;
			interpolationTime += deltaTime;
			while (frameCounter >= frameDurationMillis) {
				client.update(frameDurationMillis);
				frameCounter -= frameDurationMillis;
				interpolationTime = 0;
				NotificationPushManager.getInstance().update();
			}
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			super.render();
		} catch (Exception e) {
			dispose();
			throw e;
		}
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
		if (worldScreen != null) {
			worldScreen.dispose();
			worldScreen = null;
		}
		setScreen(screen);
		// TODO test if followign code is required
		// if (screenClass != WorldScreen.class) {
		// screen = screens.remove(WorldScreen.class);
		// if (screen != null) {
		// screen.dispose();
		// }
		// }
	}

	@Override
	public void dispose() {
		super.dispose();
		assetManager.dispose();
		client.dispose();
		if (worldScreen != null) {
			worldScreen.dispose();
			worldScreen = null;
		}
		disposeServer();
		NotificationPushManager.getInstance().stop();
		if (errorOutput != null) {
			try {
				errorOutput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void loginResponse(LoginResponse response) {
	}

	@Override
	public void initializeGame() {
		if (worldScreen != null) {
			worldScreen.dispose();
		}
		worldScreen = new WorldScreen();
		worldScreen.setWorld(client.getWorld());
		setScreen(worldScreen);
		if (client.getWorld().getType() == World.Type.CLIENT) {
			client.sendGameReady();
		}
	}

	@Override
	public void error(Throwable e) {
		Log.error("LIBGDX Error", e);
		System.exit(-1);
	}

	@Override
	public void playerFocusChanged() {
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
		if (getScreen() instanceof WorldScreen) {
			WorldScreen worldScreen = (WorldScreen) getScreen();
			setScreen(MultiplayerLobbyScreen.class);
			((MultiplayerLobbyScreen) getScreen()).showEndGameUI(worldScreen.getEndGameUI());
		} else {
			setScreen(MultiplayerLobbyScreen.class);
		}
	}

	@Override
	public void lobbyMessageReceived(LobbyMessage message) {
		if (getScreen() instanceof MultiplayerLobbyScreen) {
			((MultiplayerLobbyScreen) getScreen()).receivedLobbyMessage(message);
		} else if (getScreen() instanceof NewSingleplayerLobbyScreen) {
			((NewSingleplayerLobbyScreen) getScreen()).received(message);
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
		skipFrame = true;
	}

	public void focusChat() {
		Screen screen = getScreen();
		if (screen instanceof WorldScreen) {
			((WorldScreen) screen).getChatUI().focusTextInput();
		}
	}

	public void startServer(ServerMainArgs args) {
		disposeServer();
		server = new PixurvivalServer(args);
	}

	public void disposeServer() {
		if (server != null) {
			server.stopServer();
			server = null;
		}
	}

	@Override
	public void discovered(Collection<ItemCraft> itemCrafts) {
		if (screen instanceof WorldScreen) {
			((WorldScreen) screen).addItemCrafts(itemCrafts);
		}
	}
}
