package com.pixurvival.server.lobby;

import com.pixurvival.core.message.GameReady;
import com.pixurvival.core.message.lobby.LobbyMessage;
import com.pixurvival.core.message.lobby.LobbyPlayer;
import com.pixurvival.server.PlayerConnection;
import com.pixurvival.server.PlayerConnectionListener;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayerLobbySession implements PlayerConnectionListener {

	private LobbySession lobbySession;
	private PlayerConnection connection;
	private @Setter String teamName;
	private LobbyPlayer lobbyPlayer = new LobbyPlayer();

	public PlayerLobbySession(LobbySession lobbySession, PlayerConnection connection) {
		this.lobbySession = lobbySession;
		this.connection = connection;
		lobbyPlayer.setReady(false);
		lobbyPlayer.setPlayerName(connection.toString());
	}

	@Override
	public void handleLobbyMessage(LobbyMessage message) {
		lobbySession.received(this, message);
	}

	@Override
	public void handleDisconnected() {
		lobbySession.removePlayer(this);
	}

	@Override
	public void handleGameReady(GameReady gameReady) {
		lobbySession.receivedGameReady();
	}
}
