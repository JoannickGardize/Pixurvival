package com.pixurvival.server.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.core.message.lobby.LobbyMessage;
import com.pixurvival.server.PixurvivalServer;
import com.pixurvival.server.PlayerConnection;

import lombok.Getter;

public class LobbySession {

	private static final String DEFAULT_TEAM_NAME = "Default";

	private @Getter List<PlayerLobbySession> playerSessions = new ArrayList<>();
	private @Getter List<LobbySessionTeam> teams = new ArrayList<>();
	private @Getter PixurvivalServer server;

	private Map<Class<? extends LobbyPhase>, LobbyPhase> phases = new HashMap<>();
	private @Getter LobbyPhase currentPhase;

	public LobbySession(PixurvivalServer server) {
		this.server = server;
		teams.add(new LobbySessionTeam(DEFAULT_TEAM_NAME));
		phases.put(PreparingPhase.class, new PreparingPhase(this));
		phases.put(ContentPackCheckPhase.class, new ContentPackCheckPhase(this));
		phases.put(StartingGamePhase.class, new StartingGamePhase(this));
		setCurrentPhase(PreparingPhase.class);
	}

	@SuppressWarnings("unchecked")
	public <T extends LobbyPhase> T getPhase(Class<T> type) {
		return (T) phases.get(type);
	}

	@SuppressWarnings("unchecked")
	public <T extends LobbyPhase> T setCurrentPhase(Class<T> type) {
		currentPhase = phases.get(type);
		currentPhase.started();
		return (T) currentPhase;
	}

	public void addPlayer(PlayerConnection connection) {
		PlayerLobbySession playerSession = new PlayerLobbySession(this, connection);
		connection.addPlayerConnectionMessageListeners(playerSession);
		playerSessions.add(playerSession);
		currentPhase.playerEntered(playerSession);
	}

	public void removePlayer(PlayerLobbySession session) {
		playerSessions.remove(session);
		currentPhase.playerLeaved(session);
	}

	public void received(PlayerLobbySession playerSession, LobbyMessage lobbyMessage) {
		currentPhase.received(playerSession, lobbyMessage);
	}

	public void terminate() {
		playerSessions.forEach(s -> s.getConnection().removePlayerConnectionMessageListeners(s));
		server.removeLobbySession(this);
	}

}
