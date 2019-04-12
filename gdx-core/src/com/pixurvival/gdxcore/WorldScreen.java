package com.pixurvival.gdxcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pixurvival.core.World;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.gdxcore.drawer.DrawData;
import com.pixurvival.gdxcore.textures.ContentPackTextures;
import com.pixurvival.gdxcore.ui.CharacterUI;
import com.pixurvival.gdxcore.ui.HeldItemStackActor;
import com.pixurvival.gdxcore.ui.ItemCraftTooltip;
import com.pixurvival.gdxcore.ui.MiniMapUI;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorldScreen implements Screen {

	public static final int VIEWPORT_WORLD_WIDTH = 30;

	@Getter
	private World world;
	private Stage worldStage = new Stage(new FitViewport(VIEWPORT_WORLD_WIDTH, VIEWPORT_WORLD_WIDTH));
	private Stage hudStage = new Stage(new ScreenViewport());
	private KeyInputProcessor keyboardInputProcessor = new KeyInputProcessor(new KeyMapping());
	private CameraControlProcessor cameraControlProcessor = new CameraControlProcessor(worldStage.getViewport());
	private long myPlayerId;

	public void setWorld(World world, ContentPackTextures contentPackTextures, long myPlayerId) {
		this.myPlayerId = myPlayerId;
		this.world = world;
		worldStage.clear();
		worldStage.addActor(new MapActor(world.getMap()));
		worldStage.addActor(new EntitiesActor());
		hudStage.clear();
		HeldItemStackActor heldItemStackActor = new HeldItemStackActor();
		MiniMapUI miniMapUI = new MiniMapUI(myPlayerId);
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
		hudStage.addActor(heldItemStackActor);
		hudStage.addActor(ItemCraftTooltip.getInstance());

	}

	@Override
	public void show() {
		InputMultiplexer im = new InputMultiplexer();
		im.addProcessor(hudStage);
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
		worldStage.act();
		Entity myPlayer = world.getEntityPool().get(EntityGroup.PLAYER, myPlayerId);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
