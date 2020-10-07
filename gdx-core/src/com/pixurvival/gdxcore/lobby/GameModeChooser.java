package com.pixurvival.gdxcore.lobby;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.TranslationKey;
import com.pixurvival.core.contentPack.serialization.ContentPackSerialization;
import com.pixurvival.core.message.lobby.ChooseGameModeRequest;
import com.pixurvival.core.message.lobby.GameModeList;
import com.pixurvival.core.message.lobby.GameModeListRequest;
import com.pixurvival.core.message.lobby.LobbyData;
import com.pixurvival.core.util.Cache;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.Separator;

public class GameModeChooser extends Table {

	private List<ContentPackIdentifier> contentPackIdentifierList = new List<>(PixurvivalGame.getSkin());

	private List<String> gameModeList = new List<>(PixurvivalGame.getSkin());

	private Set<ContentPackIdentifier> availableContentPacks = new HashSet<>();
	private Cache<ContentPackIdentifier, ContentPack> contentPackCache = new Cache<>(identifier -> {
		ContentPackSerialization serialization = PixurvivalGame.getClient().getContentPackSerialization();
		try {
			return serialization.load(identifier);
		} catch (ContentPackException e) {
			Log.error(e.toString());
			return null;
		}
	});

	private Map<ContentPackIdentifier, String[]> gameModesOfMissingPacks = new HashMap<>();

	private int selectedGameModeIndex;

	private boolean programmaticChangeList;

	public GameModeChooser() {

		setBackground(PixurvivalGame.getSkin().get("panel", Drawable.class));
		defaults().expandX().pad(2).fill();
		add(new Label(PixurvivalGame.getString("lobby.contentPack"), PixurvivalGame.getSkin(), "default", Color.WHITE));
		row();
		add(new Separator()).pad(0);
		row();
		add(new ScrollPane(contentPackIdentifierList, PixurvivalGame.getSkin())).expandY();
		row();
		add(new Separator()).pad(0);
		row();
		add(new Label(PixurvivalGame.getString("lobby.gameMode"), PixurvivalGame.getSkin(), "default", Color.WHITE));
		row();
		add(new Separator()).pad(0);
		row();
		add(new ScrollPane(gameModeList, PixurvivalGame.getSkin())).expandY();

		availableContentPacks.addAll(PixurvivalGame.getClient().getContentPackSerialization().list());

		contentPackIdentifierList.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (!programmaticChangeList) {
					event.cancel();
					PixurvivalGame.getClient().send(new ChooseGameModeRequest(contentPackIdentifierList.getSelectedIndex(), 0));
				}
			}
		});
		gameModeList.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (!programmaticChangeList) {
					event.cancel();
					PixurvivalGame.getClient().send(new ChooseGameModeRequest(contentPackIdentifierList.getSelectedIndex(), gameModeList.getSelectedIndex()));
				}
			}
		});
	}

	public void setData(LobbyData data) {
		programmaticChangeList = true;
		ContentPackIdentifier selectedIdentifier = data.getAvailableContentPacks()[data.getSelectedContentPackIndex()];
		contentPackIdentifierList.setItems(data.getAvailableContentPacks());
		contentPackIdentifierList.setSelectedIndex(data.getSelectedContentPackIndex());
		selectedGameModeIndex = data.getSelectedGameModeIndex();
		String[] gameModeStrings = getGameModes(selectedIdentifier);
		if (gameModeStrings != null && gameModeStrings.length > 0) {
			gameModeList.setItems(gameModeStrings);
			gameModeList.setSelectedIndex(selectedGameModeIndex);
		} else {
			// put fake array to avoid sending multiple time the request
			gameModeList.setItems();
			gameModesOfMissingPacks.put(selectedIdentifier, new String[0]);
			java.util.List<Locale> locales = PixurvivalGame.getClient().getLocalePriorityList();
			PixurvivalGame.getClient().send(new GameModeListRequest(data.getSelectedContentPackIndex(), locales.toArray(new Locale[locales.size()])));
		}
		programmaticChangeList = false;
	}

	private String[] getGameModes(ContentPackIdentifier selectedIdentifier) {
		if (availableContentPacks.contains(selectedIdentifier)) {
			ContentPack pack = contentPackCache.get(selectedIdentifier);
			if (pack == null) {
				return new String[0];
			}
			Locale locale = PixurvivalGame.getClient().getLocaleFor(pack);
			String[] gameModeStrings = new String[pack.getGameModes().size()];
			for (int i = 0; i < pack.getGameModes().size(); i++) {
				gameModeStrings[i] = pack.getTranslation(locale, pack.getGameModes().get(i), TranslationKey.NAME);
			}
			return gameModeStrings;
		} else {
			return gameModesOfMissingPacks.get(selectedIdentifier);
		}
	}

	public void acceptGameModeList(GameModeList list) {
		gameModesOfMissingPacks.put(list.getIdentifier(), list.getGameModes());
		if (list.getIdentifier().equals(contentPackIdentifierList.getSelected())) {
			gameModeList.setItems(list.getGameModes());
			gameModeList.setSelectedIndex(selectedGameModeIndex);
		}
	}
}