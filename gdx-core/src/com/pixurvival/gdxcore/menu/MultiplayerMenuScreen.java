package com.pixurvival.gdxcore.menu;

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
import com.pixurvival.gdxcore.PixurvivalGame;

public class MultiplayerMenuScreen implements Screen {

	private Stage stage = new Stage(new ScreenViewport());
	private ConnectionMessageWindow connectionMessageWindow;

	public MultiplayerMenuScreen() {
		Skin skin = PixurvivalGame.getSkin();
		Table table = new Table();
		table.setFillParent(true);
		table.defaults().pad(5);
		table.add(new Label(PixurvivalGame.getString("menu.multiplayer.serverAddress"), skin)).right();
		TextField ipField = new TextField("127.0.0.1", skin);
		table.add(ipField);
		table.row();

		table.add(new Label(PixurvivalGame.getString("menu.multiplayer.serverPort"), skin)).right();
		TextField portField = new TextField("7777", skin);
		table.add(portField);
		table.row();

		table.add(new Label(PixurvivalGame.getString("menu.multiplayer.playerName"), skin)).right();
		TextField nameField = new TextField("Bob", skin);
		table.add(nameField);
		table.row();

		Table buttonTable = new Table();
		buttonTable.defaults().pad(0, 5, 0, 5).prefWidth(100);
		TextButton backButton = new TextButton(PixurvivalGame.getString("menu.multiplayer.back"), skin);
		buttonTable.add(backButton).center();
		TextButton connectButton = new TextButton(PixurvivalGame.getString("menu.multiplayer.connect"), skin);
		buttonTable.add(connectButton).center();

		table.add(buttonTable).colspan(2);

		connectionMessageWindow = new ConnectionMessageWindow();

		stage.addActor(table);
		stage.addActor(connectionMessageWindow);

		connectButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				connectionMessageWindow.showWaitingMessage();
				new Thread(() -> PixurvivalGame.getClient().connectToServer(ipField.getText(), Integer.valueOf(portField.getText()), nameField.getText())).start();
			}
		});

		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				PixurvivalGame.setScreen(MainMenuScreen.class);
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
		connectionMessageWindow.update(stage.getViewport());
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
