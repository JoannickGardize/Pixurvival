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
import com.pixurvival.core.EndGameData;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.playerRequest.PlayerMovementRequest;
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
import com.pixurvival.gdxcore.ui.ChatUI;
import com.pixurvival.gdxcore.ui.CraftUI;
import com.pixurvival.gdxcore.ui.EndGameUI;
import com.pixurvival.gdxcore.ui.EquipmentUI;
import com.pixurvival.gdxcore.ui.HeldItemStackActor;
import com.pixurvival.gdxcore.ui.InventoryUI;
import com.pixurvival.gdxcore.ui.MiniMapUI;
import com.pixurvival.gdxcore.ui.PauseMenu;
import com.pixurvival.gdxcore.ui.StatusBarUI;
import com.pixurvival.gdxcore.ui.TimeUI;
import com.pixurvival.gdxcore.ui.UILayoutManager;
import com.pixurvival.gdxcore.ui.tooltip.ItemCraftTooltip;
import com.pixurvival.gdxcore.ui.tooltip.ItemTooltip;
import com.pixurvival.gdxcore.util.FillActor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorldScreen implements Screen {

	public static final float CAMERA_BOUNDS = GameConstants.PLAYER_VIEW_DISTANCE - 5;
	public static final float VIEWPORT_WORLD_WIDTH = CAMERA_BOUNDS * 2;

	private @Getter World world;
	private static @Getter Stage worldStage;

	// private Stage worldStage = new Stage(new
	// ExtendViewport(VIEWPORT_WORLD_WIDTH
	// * 0.75f, VIEWPORT_WORLD_WIDTH * 0.75f, VIEWPORT_WORLD_WIDTH,
	// VIEWPORT_WORLD_WIDTH));
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

	public void setWorld(World world) {
		worldStage = new Stage(new FitViewport(VIEWPORT_WORLD_WIDTH * 0.75f, VIEWPORT_WORLD_WIDTH * 0.75f));
		cameraControlProcessor = new CameraControlProcessor(worldStage.getViewport());
		this.world = world;
		worldStage.clear();
		worldStage.addActor(new MapActor(world.getMap()));
		entitiesActor = new EntitiesActor();
		worldStage.addActor(entitiesActor);
		// worldStage.addActor(new MapAnalyticsDebugActor());
		hudStage.clear();
		hudStage.addActor(pauseUI);
		hudStage.addActor(pauseUI.getControlsPanel().getPopupWindow());
		HeldItemStackActor heldItemStackActor = new HeldItemStackActor();
		MiniMapUI miniMapUI = new MiniMapUI();
		OverlaysActor overlayActor = new OverlaysActor(worldStage.getViewport());
		hudStage.addListener(overlayActor);
		hudStage.addActor(overlayActor);
		hudStage.addActor(miniMapUI);
		miniMapUI.setPosition(0, hudStage.getHeight() - miniMapUI.getHeight());
		EquipmentUI equipmentUI = new EquipmentUI();
		hudStage.addActor(equipmentUI);
		InventoryUI inventoryUI = new InventoryUI();
		hudStage.addActor(inventoryUI);
		hudStage.addActor(craftUI);
		world.getChatManager().addListener(chatUI);
		TimeUI timeUI = new TimeUI();
		hudStage.addActor(timeUI);
		hudStage.addActor(chatUI);
		hudStage.addActor(heldItemStackActor);
		hudStage.addActor(statusBarUI);
		statusBarUI.updatePosition();
		hudStage.addActor(ItemCraftTooltip.getInstance());
		hudStage.addActor(ItemTooltip.getInstance());
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
		world.getMyPlayer().getStats().addListener(ItemTooltip.getInstance());
		defaultSoundsPlayer = new DefaultSoundsPlayer(world);
	}

	public void gameStarted() {
		if (world.getMapLimitsRun() != null) {
			worldStage.addActor(new MapLimitActor(world.getMapLimitsRun().getRectangle()));
		}
		NotificationPushManager.getInstance()
				.push(Notification.builder().status("In game").party(new Party(world.getPlayerEntities().size(), world.getPlayerEntities().size())).startTime(Instant.now().getEpochSecond()).build());
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
		cameraControlProcessor.updateCameraPosition(data == null ? myPlayer.getPosition() : data.getDrawPosition());
		worldStage.draw();
		if (!(world.getTime().getDayCycle() instanceof EternalDayCycleRun)) {
			lightDrawer.draw(worldStage);
		}

		hudStage.getViewport().apply();
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
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		worldStage.dispose();
		hudStage.dispose();
		lightDrawer.dispose();
		PixurvivalGame.getContentPackTextures().dispose();
		PixurvivalGame.getClient().getWorld().unload();
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
}
