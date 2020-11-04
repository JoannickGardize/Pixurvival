package com.pixurvival.core.message.lobby;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LobbyPlayer {

	private String playerName;
	private int selectedRole = -1;
	private boolean ready;
}
