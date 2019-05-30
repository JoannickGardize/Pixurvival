package com.pixurvival.gdxcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.gdxcore.debug.DebugInfosActor;
import com.pixurvival.gdxcore.drawer.DrawData;
import com.pixurvival.gdxcore.overlay.OverlaysActor;
import com.pixurvival.gdxcore.ui.CharacterUI;
import com.pixurvival.gdxcore.ui.ChatUI;
import com.pixurvival.gdxcore.ui.HeldItemStackActor;
import com.pixurvival.gdxcore.ui.ItemCraftTooltip;
import com.pixurvival.gdxcore.ui.MiniMapUI;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorldScreen implements Screen {

	public static final double CAMERA_BOUNDS = GameConstants.PLAYER_VIEW_DISTANCE - 5;
	public static final float VIEWPORT_WORLD_WIDTH = (float) (CAMERA_BOUNDS * 2 * 0.75);

	private @Getter World world;
	private Stage worldStage = new Stage(new FitViewport(VIEWPORT_WORLD_WIDTH, VIEWPORT_WORLD_WIDTH));
	private Stage hudStage = new Stage(new ScreenViewport());
	private KeyInputProcessor keyboardInputProcessor = new KeyInputProcessor(new KeyMapping());
	private CameraControlProcessor cameraControlProcessor = new CameraControlProcessor(worldStage.getViewport());
	private EntitiesActor entitiesActor;
	private DebugInfosActor debugInfosActors;

	public void setWorld(World world) {
		this.world = world;
		worldStage.clear();
		worldStage.addActor(new MapActor(world.getMap()));
		entitiesActor = new EntitiesActor();
		worldStage.addActor(entitiesActor);
		// worldStage.addActor(new MapAnalyticsDebugActor());
		hudStage.clear();
		HeldItemStackActor heldItemStackActor = new HeldItemStackActor();
		MiniMapUI miniMapUI = new MiniMapUI(world.getMyPlayerId());
		OverlaysActor overlayActor = new OverlaysActor(worldStage.getViewport());
		hudStage.addListener(overlayActor);
		hudStage.addActor(overlayActor);
		hudStage.addActor(miniMapUI);
		miniMapUI.setPosition(0, hudStage.getHeight() - miniMapUI.getHeight());
		miniMapUI.initialize();
		// InventoryUI inventoryUI = new InventoryUI();
		// hudStage.addActor(inventoryUI);
		// inventoryUI.initialize();
		// CraftUI craftUI = new CraftUI();
		// hudStage.addActor(craftUI);
		// craftUI.initialize();
		CharacterUI characterUI = new CharacterUI();
		hudStage.addActor(characterUI);
		characterUI.initialize();
		ChatUI chatUI = new ChatUI();
		world.getChatManager().addListener(chatUI);
		hudStage.addActor(chatUI);
		hudStage.addActor(heldItemStackActor);
		hudStage.addActor(ItemCraftTooltip.getInstance());
		debugInfosActors = new DebugInfosActor();
		debugInfosActors.setVisible(false);
		hudStage.addActor(debugInfosActors);
		PixurvivalGame.getClient().getMyInventory().addListener(ItemCraftTooltip.getInstance());
	}

	public void switchShowCollisionBoxes() {
		entitiesActor.setDebug(!entitiesActor.getDebug());
	}

	public void switchShowDebugInfos() {
		debugInfosActors.setVisible(!debugInfosActors.isVisible());
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
		im.addProcessor(new WorldMouseProcessor(worldStage));
		Gdx.input.setInputProcessor(im);
	}

	@Override
	public void render(float delta) {
		if (keyboardInputProcessor.update()) {
			PixurvivalGame.getClient().sendAction(keyboardInputProcessor.getPlayerAction());
		}
		worldStage.getViewport().apply();
		updateMouseTarget();
		worldStage.act();
		Entity myPlayer = world.getMyPlayer();
		if (myPlayer != null) {
			Object oData = myPlayer.getCustomData();
			if (oData != null) {
				DrawData data = (DrawData) oData;
				cameraControlProcessor.updateCameraPosition(data.getDrawPosition());
			}
		}
		worldStage.draw();
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
	}

	private void updateMouseTarget() {
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		if (myPlayer != null) {
			Vector2 worldPoint = worldStage.getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
			myPlayer.getTargetPosition().set(worldPoint.x, worldPoint.y);
		}
	}
}
