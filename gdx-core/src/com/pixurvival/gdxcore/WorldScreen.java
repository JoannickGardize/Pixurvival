package com.pixurvival.gdxcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pixurvival.core.Entity;
import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.World;
import com.pixurvival.gdxcore.drawer.DrawData;
import com.pixurvival.gdxcore.graphics.ContentPackTextures;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorldScreen implements Screen {

	public static final int VIEWPORT_WORLD_WIDTH = 30;

	private @NonNull PixurvivalGame game;
	@Getter
	private World world;
	private Stage worldStage = new Stage(new FitViewport(VIEWPORT_WORLD_WIDTH, VIEWPORT_WORLD_WIDTH));
	private Stage hudStage = new Stage(new ExtendViewport(16, 9));
	private KeyInputProcessor keyboardInputProcessor = new KeyInputProcessor(new KeyMapping());
	private long myPlayerId;

	public void setWorld(World world, ContentPackTextures contentPackTextures, long myPlayerId) {
		this.myPlayerId = myPlayerId;
		this.world = world;
		worldStage.clear();
		worldStage.addActor(new MapActor(world.getMap(), contentPackTextures));
		worldStage.addActor(new EntitiesActor(world.getEntityPool(), contentPackTextures));
		hudStage.clear();
		Table table = new Table();
		table.add(new MiniMapActor(world, contentPackTextures)).width(4).height(4).pad(1);
		hudStage.addActor(table);
	}

	@Override
	public void show() {
		InputMultiplexer im = new InputMultiplexer();
		im.addProcessor(keyboardInputProcessor);
		im.addProcessor(new CameraControlProcessor((OrthographicCamera) worldStage.getCamera()));
		Gdx.input.setInputProcessor(im);
	}

	@Override
	public void render(float delta) {
		if (keyboardInputProcessor.update()) {
			game.getClient().sendAction(keyboardInputProcessor.getPlayerAction());
		}

		Entity myPlayer = world.getEntityPool().get(EntityGroup.PLAYER, myPlayerId);
		if (myPlayer != null) {
			Object oData = myPlayer.getCustomData();
			if (oData != null) {
				DrawData data = (DrawData) oData;
				worldStage.getCamera().position.x = (float) data.getDrawPosition().x;
				worldStage.getCamera().position.y = (float) data.getDrawPosition().y;
			}
		}
		worldStage.getViewport().apply();
		worldStage.act();
		worldStage.draw();
		hudStage.getViewport().apply();
		hudStage.act();
		hudStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		worldStage.getViewport().update(width, height);
		hudStage.getViewport().update(width, height);
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
