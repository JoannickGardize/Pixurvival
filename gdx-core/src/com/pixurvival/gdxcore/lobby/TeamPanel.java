package com.pixurvival.gdxcore.lobby;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.core.message.lobby.ChangeTeamRequest;
import com.pixurvival.core.message.lobby.LobbyPlayer;
import com.pixurvival.core.message.lobby.RemoveTeamRequest;
import com.pixurvival.core.message.lobby.RenameTeamRequest;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.menu.MenuButton;

public class TeamPanel extends Table {

	private String teamName;
	private TextField teamNameField;
	private Table playerList;
	private TextButton joinButton;

	public TeamPanel(boolean myTeam) {
		teamNameField = new TextField("", PixurvivalGame.getSkin());

		teamNameField.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
					PixurvivalGame.getClient().send(new RenameTeamRequest(teamName, teamNameField.getText()));
					getStage().unfocusAll();
				}
				return true;
			}
		});
		playerList = new Table();
		playerList.defaults().expandX().align(Align.topLeft);
		TextButton removeButton = new MenuButton("generic.remove", () -> {
			PixurvivalGame.getClient().send(new RemoveTeamRequest(teamName));
		});

		add(teamNameField).expandX().fill();
		row();
		add(new ScrollPane(playerList, PixurvivalGame.getSkin())).expand().fill();
		row();
		if (!myTeam) {
			joinButton = new MenuButton("lobby.join", () -> {
				if (!myTeam) {
					PixurvivalGame.getClient().send(new ChangeTeamRequest(teamName));
				}
			});
			add(joinButton).fill().align(Align.center);
			row();
		}
		add(removeButton).fill().align(Align.center);
		setBackground(PixurvivalGame.getSkin().get("panel", Drawable.class));
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
		teamNameField.setText(teamName);
	}

	public void setPlayerList(LobbyPlayer[] players) {
		playerList.clear();
		for (LobbyPlayer player : players) {
			playerList.add(new PlayerRow(player));
			playerList.row();
		}
		playerList.add().expand().fill();
	}
}
