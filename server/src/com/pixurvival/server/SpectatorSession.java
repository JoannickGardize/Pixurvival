package com.pixurvival.server;

import lombok.Getter;
import lombok.Setter;

public class SpectatorSession {

	private @Getter @Setter PlayerConnection connection;
	private @Getter @Setter PlayerSession spectatedPlayer;
	private @Getter @Setter boolean newlySpectating = true;
}
