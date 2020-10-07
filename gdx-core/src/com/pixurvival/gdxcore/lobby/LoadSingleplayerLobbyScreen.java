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
import com.pixurvival.core.LoadGameException;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.menu.BackButton;
import com.pixurvival.gdxcore.menu.MessageWindow;

public class LoadSingleplayerLobbyScreen implements Screen {
	private Stage stage;

	private SaveChooser saveChooser = new SaveChooser();
	private MessageWindow errorWindow = new MessageWindow("generic.error");

	@Override
	public void show() {
		Table mainGroup = new Table();
		mainGroup.setFillParent(true);
		mainGroup.defaults().pad(2);
		errorWindow.getOkButton().setVisible(true);

		saveChooser.update();

		TextButton playButton = new TextButton(PixurvivalGame.getString("lobby.play"), PixurvivalGame.getSkin());

		playButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				try {
					PixurvivalGame.getClient().loadAndStartLocalGame(saveChooser.getSelectedSave());
				} catch (LoadGameException e) {
					LoadGameUtils.handleLoadGameError(e, errorWindow);
				}
			}
		});

		mainGroup.add(saveChooser).fill().colspan(2);
		mainGroup.row();

		mainGroup.add(new BackButton()).fill();
		mainGroup.add(playButton).fill();

		stage = new Stage(new ScreenViewport());
		stage.addActor(mainGroup);
		stage.addActor(errorWindow);
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
		errorWindow.update(stage.getViewport());
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
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
