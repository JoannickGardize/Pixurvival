package com.pixurvival.gdxcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pixurvival.core.Entity;
import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.World;
import com.pixurvival.gdxcore.graphics.ContentPackTextureAnimations;

import lombok.Getter;

public class WorldScreen implements Screen {

	public static final int VIEWPORT_WORLD_WIDTH = 20;

	@Getter
	private World world;
	private Stage stage = new Stage(new FitViewport(VIEWPORT_WORLD_WIDTH, VIEWPORT_WORLD_WIDTH));
	private KeyboardInputProcessor keyboardInputProcessor = new KeyboardInputProcessor(new KeyMapping());
	private long myPlayerId;

	public void setWorld(World world, ContentPackTextureAnimations contentPackTextureAnimations, long myPlayerId) {
		this.myPlayerId = myPlayerId;
		this.world = world;
		stage.clear();
		stage.addActor(new EntitiesActor(world.getEntityPool(), contentPackTextureAnimations));
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(keyboardInputProcessor);
	}

	@Override
	public void render(float delta) {
		Entity myPlayer = world.getEntityPool().get(EntityGroup.PLAYER, myPlayerId);
		if (myPlayer != null) {
			stage.getCamera().position.x = (float) myPlayer.getPosition().x;
			stage.getCamera().position.y = (float) myPlayer.getPosition().y;
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
