package com.pixurvival.gdxcore.menu;

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
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.lobby.LoadSingleplayerLobbyScreen;
import com.pixurvival.gdxcore.lobby.NewSingleplayerLobbyScreen;
import com.pixurvival.gdxcore.notificationpush.Notification;
import com.pixurvival.gdxcore.notificationpush.NotificationPushManager;

public class MainMenuScreen implements Screen {

	private Stage stage = new Stage(new ScreenViewport());

	public MainMenuScreen() {
		Table table = new Table();
		table.setFillParent(true);
		table.defaults().prefWidth(400).prefHeight(50).pad(5, 0, 5, 0);

		Skin skin = PixurvivalGame.getSkin();
		TextButton singleplayerNewButton = new TextButton(PixurvivalGame.getString("menu.main.singleplayer.new"), skin);
		TextButton singleplayerLoadButton = new TextButton(PixurvivalGame.getString("menu.main.singleplayer.load"), skin);
		TextButton multiplayerButton = new TextButton(PixurvivalGame.getString("menu.main.multiplayer"), skin);
		TextButton exitButton = new TextButton(PixurvivalGame.getString("menu.main.exit"), skin);

		table.add(singleplayerNewButton);
		table.row();
		table.add(singleplayerLoadButton);
		table.row();
		table.add(multiplayerButton);
		table.row();
		// table.add(editorButton);
		// table.row();
		table.add(exitButton);

		stage.addActor(table);

		singleplayerNewButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				PixurvivalGame.setScreen(NewSingleplayerLobbyScreen.class);
			}
		});

		singleplayerLoadButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				PixurvivalGame.setScreen(LoadSingleplayerLobbyScreen.class);
			}
		});

		multiplayerButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				PixurvivalGame.setScreen(MultiplayerMenuScreen.class);
			}
		});

		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		NotificationPushManager.getInstance().push(Notification.builder().status("In menus").build());
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
