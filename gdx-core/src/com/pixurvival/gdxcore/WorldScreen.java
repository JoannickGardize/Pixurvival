package com.pixurvival.gdxcore;

import java.time.Instant;
import java.util.Collection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.ActionPreconditions;
import com.pixurvival.core.EndGameData;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.message.playerRequest.PlayerMovementRequest;
import com.pixurvival.core.system.mapLimits.MapLimitsSystem;
import com.pixurvival.core.time.EternalDayCycleRun;
import com.pixurvival.gdxcore.debug.DebugInfosActor;
import com.pixurvival.gdxcore.drawer.DrawData;
import com.pixurvival.gdxcore.drawer.LightDrawer;
import com.pixurvival.gdxcore.input.CameraControlProcessor;
import com.pixurvival.gdxcore.input.InputManager;
import com.pixurvival.gdxcore.input.WorldKeyboardProcessor;
import com.pixurvival.gdxcore.input.WorldMouseProcessor;
import com.pixurvival.gdxcore.notificationpush.Notification;
import com.pixurvival.gdxcore.notificationpush.NotificationPushManager;
import com.pixurvival.gdxcore.notificationpush.Party;
import com.pixurvival.gdxcore.overlay.OverlaysActor;
import com.pixurvival.gdxcore.textures.ChunkTileTexturesManager;
import com.pixurvival.gdxcore.textures.ContentPackAssets;
import com.pixurvival.gdxcore.ui.ChatUI;
import com.pixurvival.gdxcore.ui.CraftUI;
import com.pixurvival.gdxcore.ui.EndGameUI;
import com.pixurvival.gdxcore.ui.EquipmentUI;
import com.pixurvival.gdxcore.ui.HeldItemStackActor;
import com.pixurvival.gdxcore.ui.InventoryUI;
import com.pixurvival.gdxcore.ui.MiniMapUI;
import com.pixurvival.gdxcore.ui.MouseIconActor;
import com.pixurvival.gdxcore.ui.PauseMenu;
import com.pixurvival.gdxcore.ui.RespawnTimerActor;
import com.pixurvival.gdxcore.ui.StatusBarUI;
import com.pixurvival.gdxcore.ui.TimeUI;
import com.pixurvival.gdxcore.ui.UILayoutManager;
import com.pixurvival.gdxcore.ui.interactionDialog.InteractionDialogUI;
import com.pixurvival.gdxcore.ui.tooltip.FactoryTooltip;
import com.pixurvival.gdxcore.ui.tooltip.ItemCraftTooltip;
import com.pixurvival.gdxcore.ui.tooltip.ItemTooltip;
import com.pixurvival.gdxcore.ui.tooltip.SubStatsTooltip;
import com.pixurvival.gdxcore.util.FillActor;

import lombok.Getter;

public class WorldScreen implements Screen {

	public static final float CAMERA_BOUNDS = GameConstants.PLAYER_VIEW_DISTANCE - 5;
	public static final float VIEWPORT_WORLD_WIDTH = CAMERA_BOUNDS * 2;

	private @Getter World world;
	private static @Getter Stage worldStage;

	// private Stage worldStage = new Stage(new
	// ExtendViewport(VIEWPORT_WORLD_WIDTH
	// * 0.75f, VIEWPORT_WORLD_WIDTH * 0.75f, VIEWPORT_WORLD_WIDTH,
	// VIEWPORT_WORLD_WIDTH));
	private @Getter ContentPackAssets contentPackTextures = new ContentPackAssets();
	private Stage hudStage = new Stage(new ScreenViewport());
	private WorldKeyboardProcessor keyboardInputProcessor = new WorldKeyboardProcessor();
	private CameraControlProcessor cameraControlProcessor;
	private EntitiesActor entitiesActor;
	private DebugInfosActor debugInfosActors;
	private UILayoutManager uiLayoutManager = new UILayoutManager();
	private LightDrawer lightDrawer = new LightDrawer();
	private StatusBarUI statusBarUI = new StatusBarUI();
	private @Getter EndGameUI endGameUI = new EndGameUI();
	private PauseMenu pauseUI = new PauseMenu();
	private FillActor blackPauseBackground = new FillActor(new Color(0, 0, 0, 0.5f));
	private DefaultSoundsPlayer defaultSoundsPlayer;
	private @Getter ChatUI chatUI = new ChatUI();
	private CraftUI craftUI = new CraftUI();
	private MiniMapUI miniMapUI = new MiniMapUI();
	private RespawnTimerActor respawnTimerActor = new RespawnTimerActor();
	private MouseIconActor mouseIconActor = new MouseIconActor();
	private @Getter InventoryUI inventoryUI = new InventoryUI();

	private @Getter ChunkTileTexturesManager chunkTileTexturesManager;

	public void setWorld(World world) {
		if (this.world != null) {
			throw new IllegalStateException("Cannot change world of the world screen");
		}
		contentPackTextures = new ContentPackAssets();
		try {
			// int screenWidth = Math.min(Gdx.graphics.getWidth(),
			// Gdx.graphics.getHeight());
			// int pixelWidth = Math.round(screenWidth /
			// (WorldScreen.VIEWPORT_WORLD_WIDTH *
			// GameConstants.PIXEL_PER_UNIT));
			// Seems better :
			int pixelWidth = 3;
			Log.info("Loading texture with pixel width : " + pixelWidth);
			contentPackTextures.load(world.getContentPack(), pixelWidth, PixurvivalGame.getInstance().getSoundPresets());
		} catch (ContentPackException e) {
			Log.error("Error when loading contentPack.", e);
		}
		chunkTileTexturesManager = new ChunkTileTexturesManager();
		worldStage = new Stage(new FitViewport(VIEWPORT_WORLD_WIDTH * 0.75f, VIEWPORT_WORLD_WIDTH * 0.75f));
		cameraControlProcessor = new CameraControlProcessor(worldStage.getViewport());
		this.world = world;
		worldStage.clear();
		worldStage.addActor(new TilesActor(world.getMap()));
		entitiesActor = new EntitiesActor();
		worldStage.addActor(entitiesActor);
		// worldStage.addActor(new MapAnalyticsDebugActor());
		hudStage.clear();
		hudStage.addActor(pauseUI);
		hudStage.addActor(pauseUI.getControlsPanel().getPopupWindow());
		HeldItemStackActor heldItemStackActor = new HeldItemStackActor();
		OverlaysActor overlayActor = new OverlaysActor(worldStage.getViewport());
		hudStage.addListener(overlayActor);
		hudStage.addActor(overlayActor);
		world.getEntityPool().addListener(respawnTimerActor);
		hudStage.addActor(respawnTimerActor);
		hudStage.addActor(miniMapUI);
		miniMapUI.setPosition(0, hudStage.getHeight() - miniMapUI.getHeight());
		EquipmentUI equipmentUI = new EquipmentUI();
		hudStage.addActor(equipmentUI);
		hudStage.addActor(inventoryUI);
		hudStage.addActor(craftUI);
		world.getChatManager().addListener(chatUI);
		TimeUI timeUI = new TimeUI();
		hudStage.addActor(timeUI);
		hudStage.addActor(chatUI);
		InteractionDialogUI.getInstance();
		hudStage.addActor(InteractionDialogUI.getInstance());
		hudStage.addActor(heldItemStackActor);
		hudStage.addActor(statusBarUI);
		hudStage.addActor(mouseIconActor);
		statusBarUI.updatePosition();
		hudStage.addActor(ItemCraftTooltip.getInstance());
		hudStage.addActor(FactoryTooltip.getInstance());
		hudStage.addActor(ItemTooltip.getInstance());
		hudStage.addActor(SubStatsTooltip.getInstance());
		SubStatsTooltip.getInstance().setVisible(false);
		hudStage.addActor(endGameUI);
		debugInfosActors = new DebugInfosActor();
		debugInfosActors.setVisible(false);
		hudStage.addActor(debugInfosActors);
		blackPauseBackground.setVisible(false);
		hudStage.addActor(blackPauseBackground);

		uiLayoutManager.add(chatUI, UILayoutManager.LEFT_SIDE, 30);
		uiLayoutManager.add(inventoryUI, UILayoutManager.LEFT_SIDE, 55);
		uiLayoutManager.add(equipmentUI, UILayoutManager.LEFT_SIDE, 70);
		uiLayoutManager.add(miniMapUI, UILayoutManager.LEFT_SIDE, 100);
		uiLayoutManager.add(craftUI, UILayoutManager.RIGHT_SIDE, 80);
		uiLayoutManager.add(timeUI, UILayoutManager.RIGHT_SIDE, 100);

		PixurvivalGame.getClient().getMyInventory().addListener(ItemCraftTooltip.getInstance());
		PixurvivalGame.getClient().getMyInventory().addListener(FactoryTooltip.getInstance());
		world.getMyPlayer().getStats().addListener(ItemTooltip.getInstance());
		world.getMyPlayer().getStats().addListener(ItemCraftTooltip.getInstance());

		defaultSoundsPlayer = new DefaultSoundsPlayer(world);
	}

	public void gameStarted() {
		MapLimitsSystem mapLimitsSystem = world.getSystem(MapLimitsSystem.class);
		if (mapLimitsSystem != null) {
			worldStage.addActor(new MapLimitActor(mapLimitsSystem));
		}
		if (world.getMyPlayer() != null && !world.getMyPlayer().isAlive()) {
			respawnTimerActor.playerDied(world.getMyPlayer());
		}
		NotificationPushManager.getInstance().push(Notification.builder().status("In game").party(new Party(world.getPlayerEntities().size(), world.getPlayerEntities().size()))
				.startTime(Instant.now().getEpochSecond()).build());
	}

	public void switchShowCollisionBoxes() {
		entitiesActor.setDebug(!entitiesActor.getDebug());
	}

	public void switchShowDebugInfos() {
		debugInfosActors.setVisible(!debugInfosActors.isVisible());
	}

	public void showEndGame(EndGameData data) {
		endGameUI.show(data);
	}

	@Override
	public void show() {
		InputMultiplexer im = new InputMultiplexer();
		im.addProcessor(hudStage);
		im.addProcessor(new InputAdapter() {
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				// Forces to remove focus of ChatUI TextField, or any other
				// focus-grabber
				// widgets
				hudStage.unfocusAll();
				return false;
			}
		});
		im.addProcessor(keyboardInputProcessor);
		im.addProcessor(cameraControlProcessor);
		im.addProcessor(new WorldMouseProcessor());
		Gdx.input.setInputProcessor(im);
	}

	@Override
	public void render(float delta) {
		PlayerMovementRequest request = InputManager.getInstance().updatePlayerMovement();
		if (request != null) {
			PixurvivalGame.getClient().sendAction(request);
		}
		PlayerEntity myPlayer = world.getMyPlayer();
		worldStage.getViewport().apply();
		updateMouseTarget();
		worldStage.act();
		DrawData data = (DrawData) myPlayer.getCustomData();
		// TODO It's the void bug?
		cameraControlProcessor.updateCameraPosition(data == null ? myPlayer.getPosition() : data.getDrawPosition());
		worldStage.draw();
		if (!(world.getTime().getDayCycle() instanceof EternalDayCycleRun)) {
			lightDrawer.draw(worldStage);
		}

		hudStage.getViewport().apply();
		Vector2 position = getWorldCursorPosition();
		StructureEntity structure = myPlayer.getWorld().getMap().findClosestStructure(new com.pixurvival.core.util.Vector2(position.x, position.y),
				GameConstants.MAX_STRUCTURE_INTERACTION_DISTANCE);
		setMouseInteractionIconVisible(ActionPreconditions.canInteract(myPlayer, structure));
		hudStage.act();
		hudStage.draw();
		defaultSoundsPlayer.playSounds();
	}

	@Override
	public void resize(int width, int height) {
		worldStage.getViewport().update(width, height);
		ScreenResizeEvent event = new ScreenResizeEvent();
		event.setPrevScreenWidth(hudStage.getViewport().getScreenWidth());
		event.setPrevScreenHeight(hudStage.getViewport().getScreenHeight());
		event.setNewScreenWidth(width);
		event.setNewScreenHeight(height);
		hudStage.getViewport().update(width, height, true);
		if (event.isValid()) {
			hudStage.getRoot().fire(event);
		}
		uiLayoutManager.resize(width, height, ((FitViewport) worldStage.getViewport()).getLeftGutterWidth());
		lightDrawer.resize(width, height);
		statusBarUI.updatePosition();
		endGameUI.update(hudStage.getViewport());
		pauseUI.update();
		respawnTimerActor.setPosition(width / 2f, height - height / 3f);
		InteractionDialogUI.getInstance().sizeAndPosition();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		worldStage.dispose();
		hudStage.dispose();
		lightDrawer.dispose();
		contentPackTextures.dispose();
		PixurvivalGame.getClient().getWorld().unload();
		chunkTileTexturesManager.setRunning(false);
		chunkTileTexturesManager.dispose();
		miniMapUI.dispose();
		world.unload();
	}

	private void updateMouseTarget() {
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		Vector2 worldPoint = worldStage.getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		myPlayer.getTargetPosition().set(worldPoint.x, worldPoint.y);
	}

	public void switchPauseMenu() {
		pauseUI.toFront();
		boolean pausing = !pauseUI.isVisible();
		pauseUI.setVisible(pausing);
		blackPauseBackground.setVisible(pausing);
		PixurvivalGame.getClient().requestPause(pausing);
	}

	public void addItemCrafts(Collection<ItemCraft> crafts) {
		craftUI.addItemCrafts(crafts);
	}

	public void setMouseInteractionIconVisible(boolean visible) {
		mouseIconActor.setVisible(visible);
	}

	public static Vector2 getWorldCursorPosition() {
		return WorldScreen.getWorldStage().getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
	}
}
