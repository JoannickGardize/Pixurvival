package com.pixurvival.gdxcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
	private Stage stage = new Stage(new FitViewport(VIEWPORT_WORLD_WIDTH, VIEWPORT_WORLD_WIDTH));
	private KeyboardInputProcessor keyboardInputProcessor = new KeyboardInputProcessor(new KeyMapping());
	private long myPlayerId;

	public void setWorld(World world, ContentPackTextures contentPackTextures, long myPlayerId) {
		this.myPlayerId = myPlayerId;
		this.world = world;
		stage.clear();
		stage.addActor(new MapActor(world.getMap(), contentPackTextures));
		stage.addActor(new EntitiesActor(world.getEntityPool(), contentPackTextures));
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(keyboardInputProcessor);
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
				stage.getCamera().position.x = (float) data.getDrawPosition().x;
				stage.getCamera().position.y = (float) data.getDrawPosition().y;
			}
		}
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
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
