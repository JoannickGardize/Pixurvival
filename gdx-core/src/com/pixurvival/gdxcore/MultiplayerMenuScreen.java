package com.pixurvival.gdxcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MultiplayerMenuScreen implements Screen {

	private Stage stage = new Stage(new ScreenViewport());
	private PixurvivalGame game;

	public MultiplayerMenuScreen(PixurvivalGame game) {
		this.game = game;

		Table table = new Table();
		table.setFillParent(true);
		table.defaults().pad(5);
		Skin skin = game.getAssetManager().get(Assets.SKIN, Skin.class);
		table.add(new Label(game.getString("menu.multiplayer.serverAddress"), skin)).right();
		TextField ipField = new TextField("127.0.0.1", skin);
		table.add(ipField);
		table.row();

		table.add(new Label(game.getString("menu.multiplayer.serverPort"), skin)).right();
		TextField portField = new TextField("7777", skin);
		table.add(portField);
		table.row();

		table.add(new Label(game.getString("menu.multiplayer.playerName"), skin)).right();
		TextField nameButton = new TextField("Bob", skin);
		table.add(nameButton);
		table.row();

		Table buttonTable = new Table();
		buttonTable.defaults().pad(0, 5, 0, 5);
		TextButton backButton = new TextButton(game.getString("menu.multiplayer.back"), skin);
		buttonTable.add(backButton).center();
		TextButton connectButton = new TextButton(game.getString("menu.multiplayer.connect"), skin);
		buttonTable.add(connectButton).center();

		table.add(buttonTable).colspan(5);

		stage.addActor(table);

		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(MainMenuScreen.class);
			}
		});
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
		stage.dispose();
	}

}
