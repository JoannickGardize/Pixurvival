package com.pixurvival.server.lobby;

import com.pixurvival.core.message.lobby.LobbyMessage;
import com.pixurvival.server.PlayerConnection;
import com.pixurvival.server.PlayerConnectionListener;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayerLobbySession implements PlayerConnectionListener {

	private LobbySession lobbySession;
	private PlayerConnection connection;
	private @Setter String teamName;
	private @Setter boolean ready;

	public PlayerLobbySession(LobbySession lobbySession, PlayerConnection connection) {
		this.lobbySession = lobbySession;
		this.connection = connection;
	}

	@Override
	public void handleLobbyMessage(LobbyMessage message) {
		lobbySession.received(this, message);
	}

	@Override
	public void handleDisconnected() {
		lobbySession.removePlayer(this);
	}
}
