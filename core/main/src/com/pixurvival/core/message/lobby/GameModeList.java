package com.pixurvival.core.message.lobby;

import com.pixurvival.core.contentPack.ContentPackIdentifier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameModeList extends LobbyMessage {
	private ContentPackIdentifier identifier;
	private String[] gameModes;
}