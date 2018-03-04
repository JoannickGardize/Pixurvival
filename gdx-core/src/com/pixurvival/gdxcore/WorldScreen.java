package com.pixurvival.gdxcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pixurvival.core.Entity;
import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.World;
import com.pixurvival.gdxcore.drawer.DrawData;
import com.pixurvival.gdxcore.graphics.ContentPackTextures;
import com.pixurvival.gdxcore.ui.InventoryUI;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorldScreen implements Screen {

	public static final int VIEWPORT_WORLD_WIDTH = 25;

	@Getter
	private World world;
	private Stage worldStage = new Stage(new FitViewport(VIEWPORT_WORLD_WIDTH, VIEWPORT_WORLD_WIDTH));
	private Stage hudStage = new Stage(new ExtendViewport(1600, 900));
	private KeyInputProcessor keyboardInputProcessor = new KeyInputProcessor(new KeyMapping());
	private CameraControlProcessor cameraControlProcessor = new CameraControlProcessor(worldStage.getViewport());
	private long myPlayerId;
	private Table table;

	public void setWorld(World world, ContentPackTextures contentPackTextures, long myPlayerId) {
		this.myPlayerId = myPlayerId;
		this.world = world;
		worldStage.clear();
		worldStage.addActor(new MapActor(world.getMap()));
		worldStage.addActor(new EntitiesActor(world.getEntityPool()));
		hudStage.clear();
		table = new Table();
		table.setWidth(1600);
		table.setHeight(900);
		table.add(new MiniMapActor(world, contentPackTextures)).expand().top().left().width(200).height(200).pad(50);
		hudStage.addActor(table);
		hudStage.addActor(new InventoryUI());
	}

	@Override
	public void show() {
		InputMultiplexer im = new InputMultiplexer();
		im.addProcessor(hudStage);
		im.addProcessor(keyboardInputProcessor);
		im.addProcessor(cameraControlProcessor);
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
		hudStage.getViewport().update(width, height, true);
		table.setWidth(hudStage.getViewport().getWorldWidth());
		table.setHeight(hudStage.getViewport().getWorldHeight());
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
