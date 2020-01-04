package com.pixurvival.gdxcore.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pixurvival.core.message.lobby.LobbyData;
import com.pixurvival.core.message.lobby.LobbyMessage;
import com.pixurvival.gdxcore.PixurvivalGame;

public class SingleplayerLobbyScreen implements Screen {
	private Stage stage;

	private GameModeChooser gameModeChooser;

	private LobbyData lobbyDataToApply = null;

	@Override
	public void show() {
		Table mainGroup = new Table();
		mainGroup.setFillParent(true);

		gameModeChooser = new GameModeChooser();
		gameModeChooser.setData(PixurvivalGame.getClient().getSinglePlayerLobbyData());
		mainGroup.add(gameModeChooser);
		mainGroup.row();

		TextButton playButton = new TextButton(PixurvivalGame.getString("lobby.play"), PixurvivalGame.getSkin());

		playButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				PixurvivalGame.getClient().startLocalGame();
			}
		});

		mainGroup.add(playButton).fill().pad(4);

		stage = new Stage(new ScreenViewport());
		stage.addActor(mainGroup);
		Gdx.input.setInputProcessor(stage);
	}

	public void received(LobbyMessage message) {
		if (message instanceof LobbyData) {
			lobbyDataToApply = (LobbyData) message;
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		if (lobbyDataToApply != null) {
			gameModeChooser.setData(lobbyDataToApply);
			lobbyDataToApply = null;
		}
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
		dispose();
	}

	@Override
	public void dispose() {
		if (stage != null) {
			stage.dispose();
			stage = null;
		}
	}
}
