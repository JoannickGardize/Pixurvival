package com.pixurvival.gdxcore;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.pixurvival.core.World;

import lombok.Getter;

public class WorldScreen implements Screen {

	@Getter
	private World world;
	private Stage stage;

	public void setWorld(World world) {
		stage.clear();
		this.world = world;
		stage.addActor(new EntitiesActor(world.getEntityPool()));
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

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
