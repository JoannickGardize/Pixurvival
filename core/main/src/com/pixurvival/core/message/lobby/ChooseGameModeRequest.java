package com.pixurvival.core.message.lobby;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChooseGameModeRequest extends LobbyRequest {

	private int contentPackIndex;
	private int gameModeIndex;
}
