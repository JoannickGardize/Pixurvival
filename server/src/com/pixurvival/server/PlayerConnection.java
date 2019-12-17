package com.pixurvival.server;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryonet.Connection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerConnection extends Connection {

	private @Getter List<PlayerConnectionListener> playerConnectionListeners = new ArrayList<>();
	private boolean logged = false;

	public void addPlayerConnectionMessageListeners(PlayerConnectionListener playerConnectionMessageListener) {
		playerConnectionListeners.add(playerConnectionMessageListener);
	}

	public void removePlayerConnectionMessageListeners(PlayerConnectionListener playerConnectionMessageListener) {
		playerConnectionListeners.remove(playerConnectionMessageListener);
	}

	public void disconnected() {
		playerConnectionListeners.forEach(PlayerConnectionListener::handleDisconnected);
	}
}
