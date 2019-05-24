package com.pixurvival.gdxcore;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class TestApp implements ApplicationListener {

	private Texture texture;

	private Stage stage;

	@Override
	public void create() {
		texture = new Texture(Gdx.files.internal("badlogic.jpg"));
		stage = new Stage(new FitViewport(2, 2));
		stage.addActor(new Actor() {
			@Override
			public void draw(Batch batch, float parentAlpha) {
				batch.draw(texture, 0, 0, 1, 1);
			}
		});
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}
