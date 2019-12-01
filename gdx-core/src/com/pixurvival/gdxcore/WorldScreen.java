package com.pixurvival.gdxcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pixurvival.core.EndGameData;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.playerRequest.PlayerMovementRequest;
import com.pixurvival.gdxcore.debug.DebugInfosActor;
import com.pixurvival.gdxcore.drawer.DrawData;
import com.pixurvival.gdxcore.drawer.LightDrawer;
import com.pixurvival.gdxcore.input.CameraControlProcessor;
import com.pixurvival.gdxcore.input.InputManager;
import com.pixurvival.gdxcore.input.WorldKeyboardProcessor;
import com.pixurvival.gdxcore.input.WorldMouseProcessor;
import com.pixurvival.gdxcore.overlay.OverlaysActor;
import com.pixurvival.gdxcore.ui.ChatUI;
import com.pixurvival.gdxcore.ui.CraftUI;
import com.pixurvival.gdxcore.ui.EndGameUI;
import com.pixurvival.gdxcore.ui.EquipmentUI;
import com.pixurvival.gdxcore.ui.HeldItemStackActor;
import com.pixurvival.gdxcore.ui.InventoryUI;
import com.pixurvival.gdxcore.ui.MiniMapUI;
import com.pixurvival.gdxcore.ui.StatusUI;
import com.pixurvival.gdxcore.ui.TimeUI;
import com.pixurvival.gdxcore.ui.UILayoutManager;
import com.pixurvival.gdxcore.ui.tooltip.ItemCraftTooltip;
import com.pixurvival.gdxcore.ui.tooltip.ItemTooltip;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorldScreen implements Screen {

	public static final float CAMERA_BOUNDS = GameConstants.PLAYER_VIEW_DISTANCE - 5;
	public static final float VIEWPORT_WORLD_WIDTH = CAMERA_BOUNDS * 2;

	private @Getter World world;
	private static @Getter Stage worldStage = new Stage(new FitViewport(VIEWPORT_WORLD_WIDTH * 0.75f, VIEWPORT_WORLD_WIDTH * 0.75f));

	// private Stage worldStage = new Stage(new
	// ExtendViewport(VIEWPORT_WORLD_WIDTH
	// * 0.75f, VIEWPORT_WORLD_WIDTH * 0.75f, VIEWPORT_WORLD_WIDTH,
	// VIEWPORT_WORLD_WIDTH));
	private Stage hudStage = new Stage(new ScreenViewport());
	private WorldKeyboardProcessor keyboardInputProcessor = new WorldKeyboardProcessor();
	private CameraControlProcessor cameraControlProcessor = new CameraControlProcessor(worldStage.getViewport());
	private EntitiesActor entitiesActor;
	private DebugInfosActor debugInfosActors;
	private UILayoutManager uiLayoutManager = new UILayoutManager();
	private LightDrawer lightDrawer = new LightDrawer();
	private StatusUI statusUI = new StatusUI();
	private EndGameUI endGameUI = new EndGameUI();

	public void setWorld(World world) {
		this.world = world;
		worldStage.clear();
		worldStage.addActor(new MapActor(world.getMap()));
		entitiesActor = new EntitiesActor();
		worldStage.addActor(entitiesActor);
		// worldStage.addActor(new MapAnalyticsDebugActor());
		hudStage.clear();
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
		CraftUI craftUI = new CraftUI();
		hudStage.addActor(craftUI);
		ChatUI chatUI = new ChatUI();
		world.getChatManager().addListener(chatUI);
		TimeUI timeUI = new TimeUI();
		hudStage.addActor(timeUI);
		hudStage.addActor(chatUI);
		hudStage.addActor(heldItemStackActor);
		hudStage.addActor(statusUI);
		statusUI.updatePosition();
		hudStage.addActor(ItemCraftTooltip.getInstance());
		hudStage.addActor(ItemTooltip.getInstance());
		hudStage.addActor(endGameUI);
		debugInfosActors = new DebugInfosActor();
		debugInfosActors.setVisible(false);
		hudStage.addActor(debugInfosActors);

		uiLayoutManager.add(chatUI, UILayoutManager.LEFT_SIDE, 30);
		uiLayoutManager.add(inventoryUI, UILayoutManager.LEFT_SIDE, 55);
		uiLayoutManager.add(equipmentUI, UILayoutManager.LEFT_SIDE, 70);
		uiLayoutManager.add(miniMapUI, UILayoutManager.LEFT_SIDE, 100);
		uiLayoutManager.add(craftUI, UILayoutManager.RIGHT_SIDE, 80);
		uiLayoutManager.add(timeUI, UILayoutManager.RIGHT_SIDE, 100);

		PixurvivalGame.getClient().getMyInventory().addListener(ItemCraftTooltip.getInstance());
		world.getMyPlayer().getStats().addListener(ItemTooltip.getInstance());
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
		worldStage.getViewport().apply();
		updateMouseTarget();
		worldStage.act();
		Entity myPlayer = world.getMyPlayer();
		if (myPlayer != null) {
			DrawData data = (DrawData) myPlayer.getCustomData();
			cameraControlProcessor.updateCameraPosition(data == null ? myPlayer.getPosition() : data.getDrawPosition());
		}
		worldStage.draw();
		lightDrawer.draw(worldStage);

		hudStage.getViewport().apply();
		hudStage.act();
		hudStage.draw();
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
		statusUI.updatePosition();
		endGameUI.update(hudStage.getViewport());
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
		PixurvivalGame.getContentPackTextures().dispose();
		PixurvivalGame.getClient().getWorld().unload();
	}

	private void updateMouseTarget() {
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		Vector2 worldPoint = worldStage.getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		myPlayer.getTargetPosition().set(worldPoint.x, worldPoint.y);
	}
}
