package com.pixurvival.core.message.lobby;

import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameModeListRequest extends LobbyRequest {

	private int contentPackIndex;
	private Locale[] requestedLocales;
}
