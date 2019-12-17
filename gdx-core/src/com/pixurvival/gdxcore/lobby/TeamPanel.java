package com.pixurvival.gdxcore.lobby;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.core.message.lobby.LobbyPlayer;
import com.pixurvival.gdxcore.PixurvivalGame;

public class TeamPanel extends Table {

	private TextField teamNameField;
	private Table playerList;

	public TeamPanel() {
		teamNameField = new TextField("", PixurvivalGame.getSkin());

		playerList = new Table();
		playerList.defaults().expandX().align(Align.left);

		add(teamNameField).expandX().fill();
		row();
		add(playerList).expand().fill();
		setBackground(PixurvivalGame.getSkin().get("panel", Drawable.class));
	}

	public void setTeamName(String teamName) {
		teamNameField.setText(teamName);
	}

	public void setPlayerList(LobbyPlayer[] players) {
		playerList.clear();
		for (LobbyPlayer player : players) {
			playerList.add(new PlayerRow(player));
			playerList.row();
		}
	}
}
