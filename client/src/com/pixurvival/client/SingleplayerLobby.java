package com.pixurvival.client;

import java.util.List;

import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.message.lobby.ChooseGameModeRequest;
import com.pixurvival.core.message.lobby.LobbyData;

import lombok.Getter;

public class SingleplayerLobby {

	private PixurvivalClient client;

	private ContentPackIdentifier[] availableContentPacks;
	private int selectedContentPackIndex = 0;
	private @Getter int selectedGameModeIndex = 0;

	public SingleplayerLobby(PixurvivalClient client) {
		this.client = client;
		List<ContentPackIdentifier> list = client.getContentPackSerialization().list();
		availableContentPacks = list.toArray(new ContentPackIdentifier[list.size()]);
		if (availableContentPacks.length == 0) {
			throw new IllegalStateException("No ContentPack available at " + client.getContentPackSerialization().getWorkingDirectory());
		}
	}

	public ContentPackIdentifier getSelectedContentPackIdentifier() {
		return availableContentPacks[selectedContentPackIndex];
	}

	public void handle(ChooseGameModeRequest request) {
		selectedContentPackIndex = request.getContentPackIndex();
		selectedGameModeIndex = request.getGameModeIndex();
		client.notify(l -> l.lobbyMessageReceived(getLobbyData()));
	}

	public LobbyData getLobbyData() {
		LobbyData data = new LobbyData();
		data.setAvailableContentPacks(availableContentPacks);
		data.setSelectedContentPackIndex(selectedContentPackIndex);
		data.setSelectedGameModeIndex(selectedGameModeIndex);
		return data;
	}
}
