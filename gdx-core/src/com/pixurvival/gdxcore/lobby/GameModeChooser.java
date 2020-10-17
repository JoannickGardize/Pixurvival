package com.pixurvival.gdxcore.lobby;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.pixurvival.core.contentPack.summary.ContentPackSummary;
import com.pixurvival.core.contentPack.summary.GameModeSummary;
import com.pixurvival.core.message.lobby.ChooseGameModeRequest;
import com.pixurvival.core.message.lobby.LobbyData;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.Separator;

public class GameModeChooser extends Table {

	private List<ContentPackSummary> contentPackList = new List<>(PixurvivalGame.getSkin());

	private List<String> gameModeList = new List<>(PixurvivalGame.getSkin());

	private int selectedGameModeIndex;

	private boolean programmaticChangeList;

	public GameModeChooser() {

		setBackground(PixurvivalGame.getSkin().get("panel", Drawable.class));
		defaults().expandX().pad(2).fill();
		add(new Label(PixurvivalGame.getString("lobby.contentPack"), PixurvivalGame.getSkin(), "default", Color.WHITE));
		row();
		add(new Separator()).pad(0);
		row();
		add(new ScrollPane(contentPackList, PixurvivalGame.getSkin())).expandY();
		row();
		add(new Separator()).pad(0);
		row();
		add(new Label(PixurvivalGame.getString("lobby.gameMode"), PixurvivalGame.getSkin(), "default", Color.WHITE));
		row();
		add(new Separator()).pad(0);
		row();
		add(new ScrollPane(gameModeList, PixurvivalGame.getSkin())).expandY();

		contentPackList.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (!programmaticChangeList) {
					event.cancel();
					PixurvivalGame.getClient().send(new ChooseGameModeRequest(contentPackList.getSelectedIndex(), 0));
				}
			}
		});
		gameModeList.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (!programmaticChangeList) {
					event.cancel();
					PixurvivalGame.getClient().send(new ChooseGameModeRequest(contentPackList.getSelectedIndex(), gameModeList.getSelectedIndex()));
				}
			}
		});
	}

	public void setData(LobbyData data) {
		programmaticChangeList = true;
		ContentPackSummary selectedPack = data.getAvailableContentPacks()[data.getSelectedContentPackIndex()];
		contentPackList.setItems(data.getAvailableContentPacks());
		contentPackList.setSelectedIndex(data.getSelectedContentPackIndex());
		selectedGameModeIndex = data.getSelectedGameModeIndex();
		String[] gameModeStrings = getGameModeStrings(selectedPack);
		gameModeList.setItems(gameModeStrings);
		gameModeList.setSelectedIndex(selectedGameModeIndex);
		programmaticChangeList = false;
	}

	public int countLobbyMaxPlayer() {
		if (selectedGameModeIndex == -1) {
			return 0;
		}
		GameModeSummary gms = contentPackList.getSelected().getGameModeSummaries()[selectedGameModeIndex];
		return gms.getTeamNumberInterval().getMax() * gms.getTeamSizeInterval().getMax();
	}

	private String[] getGameModeStrings(ContentPackSummary selectedPack) {
		if (selectedPack.getGameModeSummaries().length == 0) {
			return new String[0];
		}
		String localeTag = PixurvivalGame.getClient().getLocaleFor(selectedPack.getGameModeSummaries()[0].getNameTranslations().keySet()).toLanguageTag();
		String[] gameModeStrings = new String[selectedPack.getGameModeSummaries().length];
		for (int i = 0; i < selectedPack.getGameModeSummaries().length; i++) {
			gameModeStrings[i] = selectedPack.getGameModeSummaries()[i].getNameTranslations().get(localeTag);
		}
		return gameModeStrings;
	}
}