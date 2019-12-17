package com.pixurvival.gdxcore.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pixurvival.core.message.lobby.LobbyTeam;
import com.pixurvival.gdxcore.PixurvivalGame;

public class LobbyScreen implements Screen {

	public static final int MAX_TEAM_PER_ROW = 3;

	private Stage stage;
	private GameModeChooser gameModeChooser;
	private Table teamsTable;

	@Override
	public void show() {
		Table mainGroup = new Table();
		mainGroup.setFillParent(true);

		gameModeChooser = new GameModeChooser();
		mainGroup.add(gameModeChooser).expandY().fill();

		teamsTable = new Table();
		teamsTable.defaults().expand().fill();
		mainGroup.add(teamsTable).expand().fill();

		mainGroup.row();

		TextButton addTeamButton = new TextButton(PixurvivalGame.getString("lobby.addTeam"), PixurvivalGame.getSkin());

		addTeamDialog = new Dialog(PixurvivalGame.getString("lobby.addTeam"), PixurvivalGame.getSkin());
		addTeamDialog.

				addTeamButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {

					}
				});

		stage = new Stage(new ScreenViewport());
		stage.addActor(mainGroup);
		Gdx.input.setInputProcessor(stage);
	}

	public void setTeams(LobbyTeam[] teams) {
		teamsTable.clear();

		int count = 0;
		for (LobbyTeam team : teams) {
			TeamPanel teamPanel = new TeamPanel();
			teamPanel.setTeamName(team.getName());
			teamPanel.setPlayerList(team.getMembers());
			teamsTable.add(teamPanel);
			count++;
			if (count > MAX_TEAM_PER_ROW) {
				count = 0;
				teamsTable.row();
			}
		}
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
