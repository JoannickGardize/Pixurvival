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
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MultiplayerMenuScreen implements Screen {

	private Stage stage = new Stage(new ScreenViewport());
	private PixurvivalGame game;
	private Window connectionWindow;
	private Label connectionInfoLabel;

	public MultiplayerMenuScreen(PixurvivalGame game) {
		this.game = game;
		Skin skin = game.getAssetManager().get(Assets.SKIN, Skin.class);

		Table table = new Table();
		table.setFillParent(true);
		table.defaults().pad(5);
		table.add(new Label(game.getString("menu.multiplayer.serverAddress"), skin)).right();
		TextField ipField = new TextField("127.0.0.1", skin);
		table.add(ipField);
		table.row();

		table.add(new Label(game.getString("menu.multiplayer.serverPort"), skin)).right();
		TextField portField = new TextField("7777", skin);
		table.add(portField);
		table.row();

		table.add(new Label(game.getString("menu.multiplayer.playerName"), skin)).right();
		TextField nameField = new TextField("Bob", skin);
		table.add(nameField);
		table.row();

		Table buttonTable = new Table();
		buttonTable.defaults().pad(0, 5, 0, 5).prefWidth(100);
		TextButton backButton = new TextButton(game.getString("menu.multiplayer.back"), skin);
		buttonTable.add(backButton).center();
		TextButton connectButton = new TextButton(game.getString("menu.multiplayer.connect"), skin);
		buttonTable.add(connectButton).center();

		table.add(buttonTable).colspan(2);

		connectionWindow = new Window(game.getString("menu.multiplayer.connectWindow.title"), skin);
		connectionWindow.setModal(true);
		// connectionWindow.setVisible(false);
		connectionWindow.setMovable(false);
		connectionWindow.setResizable(false);

		connectionInfoLabel = new Label("Wesh wehs dsf sf fs dgfd g fdgh dh dgfh gfhg fh gf hg fh g", skin);
		connectionInfoLabel.setWrap(true);

		// Table connectionWindowTable = new Table();
		// connectionWindowTable.setFillParent(true);
		// connectionWindow.add(connectionWindowTable);
		connectionWindow.add(connectionInfoLabel).expand().top().width(connectionWindow.getWidth());
		connectionWindow.row();
		connectionWindow.add(new TextButton("yo", skin));

		stage.addActor(table);
		stage.addActor(connectionWindow);

		connectButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.getClient().connectToServer(ipField.getText(), Integer.valueOf(portField.getText()),
						nameField.getText());
				connectionWindow.setVisible(true);
			}
		});
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
		connectionWindow.setPosition(stage.getViewport().getWorldWidth() / 2 - connectionWindow.getWidth() / 2,
				stage.getViewport().getWorldHeight() / 2 - connectionWindow.getHeight() / 2);
		connectionWindow.getCell(connectionInfoLabel).width(connectionWindow.getWidth() - 20);
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
