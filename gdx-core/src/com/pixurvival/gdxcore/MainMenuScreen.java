package com.pixurvival.gdxcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {

	private Stage stage = new Stage(new ScreenViewport());
	private PixurvivalGame game;

	public MainMenuScreen(PixurvivalGame game) {
		this.game = game;
		Table table = new Table();
		table.setFillParent(true);
		Skin skin = new Skin(Gdx.files.internal("kenney-pixel/skin/skin.json"));

		TextButton singleplayerButton = new TextButton(game.getString("menu.main.singleplayer"), skin);
		TextButton multiplayerButton = new TextButton(game.getString("menu.main.multiplayer"), skin);
		TextButton exitButton = new TextButton(game.getString("menu.main.exit"), skin);

		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});

		table.add(singleplayerButton).prefWidth(400).prefHeight(50).padBottom(10);
		table.row();
		table.add(multiplayerButton).prefWidth(400).prefHeight(50).padBottom(10);
		table.row();
		table.add(exitButton).prefWidth(400).prefHeight(50);

		stage.addActor(table);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
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
		stage.dispose();
	}

}
