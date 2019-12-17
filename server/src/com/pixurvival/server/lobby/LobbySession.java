package com.pixurvival.server.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.PlayerInformation;
import com.pixurvival.core.message.StartGame;
import com.pixurvival.core.message.TeamComposition;
import com.pixurvival.core.message.lobby.ChangeTeamRequest;
import com.pixurvival.core.message.lobby.CreateTeamRequest;
import com.pixurvival.core.message.lobby.EnterLobby;
import com.pixurvival.core.message.lobby.LobbyList;
import com.pixurvival.core.message.lobby.LobbyMessage;
import com.pixurvival.core.message.lobby.LobbyPlayer;
import com.pixurvival.core.message.lobby.LobbyTeam;
import com.pixurvival.core.message.lobby.ReadyRequest;
import com.pixurvival.core.message.lobby.RemoveTeamRequest;
import com.pixurvival.core.message.lobby.RenameTeamRequest;
import com.pixurvival.core.team.Team;
import com.pixurvival.server.GameSession;
import com.pixurvival.server.PixurvivalServer;
import com.pixurvival.server.PlayerConnection;

public class LobbySession {

	private static final String DEFAULT_TEAM_NAME = "Default";

	private List<PlayerLobbySession> playerSessions = new ArrayList<>();
	private ContentPack selectedContentPack;
	private int selectedGameModeId;
	private Map<String, Set<PlayerLobbySession>> teams = new HashMap<>();
	private PixurvivalServer server;

	public LobbySession(PixurvivalServer server) {
		this.server = server;
		createTeam(DEFAULT_TEAM_NAME);
	}

	public void addPlayer(PlayerConnection connection) {
		PlayerLobbySession playerSession = new PlayerLobbySession(this, connection);
		connection.addPlayerConnectionMessageListeners(playerSession);
		playerSessions.add(playerSession);
		changeTeam(playerSession, teams.keySet().iterator().next());
		setAllNotReady();
		playerSession.getConnection().sendTCP(new EnterLobby());
		playerListChanged();
	}

	public void removePlayer(PlayerLobbySession session) {
		playerSessions.remove(session);
		Optional.ofNullable(teams.get(session.getTeamName())).ifPresent(t -> t.remove(session));
		setAllNotReady();
		playerListChanged();
	}

	public void received(PlayerLobbySession playerSession, LobbyMessage lobbyMessage) {
		if (lobbyMessage instanceof ChangeTeamRequest) {
			changeTeam(playerSession, ((ChangeTeamRequest) lobbyMessage).getTeamName());
		} else if (lobbyMessage instanceof ReadyRequest) {
			changeReady(playerSession, ((ReadyRequest) lobbyMessage).isReady());
		} else if (lobbyMessage instanceof CreateTeamRequest) {
			createTeam(((CreateTeamRequest) lobbyMessage).getTeamName());
		} else if (lobbyMessage instanceof RenameTeamRequest) {
			RenameTeamRequest renameTeamRequest = (RenameTeamRequest) lobbyMessage;
			renameTeam(renameTeamRequest.getOldName(), renameTeamRequest.getNewName());
		} else if (lobbyMessage instanceof RemoveTeamRequest) {
			removeTeam(((RemoveTeamRequest) lobbyMessage).getTeamName());
		}
	}

	private void createTeam(String teamName) {
		teams.computeIfAbsent(teamName, n -> new HashSet<>());
	}

	private void renameTeam(String oldName, String newName) {
		Set<PlayerLobbySession> team = teams.remove(oldName);
		if (team != null && !teams.containsKey(newName)) {
			teams.put(newName, team);
			team.forEach(p -> p.setTeamName(newName));
		}
	}

	private void removeTeam(String teamName) {
		if (teams.size() > 1) {
			Set<PlayerLobbySession> team = teams.remove(teamName);
			Entry<String, Set<PlayerLobbySession>> newTeam = teams.entrySet().iterator().next();
			if (!team.isEmpty()) {
				team.forEach(p -> {
					p.setTeamName(newTeam.getKey());
					newTeam.getValue().add(p);
				});
				setAllNotReady();
				playerListChanged();
			}
		}
	}

	private void changeTeam(PlayerLobbySession playerSession, String newTeamName) {
		String oldTeamName = playerSession.getTeamName();
		Set<PlayerLobbySession> newTeam = teams.get(newTeamName);
		if (!Objects.equals(newTeamName, oldTeamName) && newTeam != null) {
			playerSession.setTeamName(newTeamName);
			Optional.ofNullable(teams.get(oldTeamName)).ifPresent(t -> t.remove(playerSession));
			newTeam.add(playerSession);
			setAllNotReady();
			playerListChanged();
		}
	}

	private void changeReady(PlayerLobbySession playerSession, boolean ready) {
		if (playerSession.isReady() != ready) {
			playerSession.setReady(ready);
			playerListChanged();
			if (ready) {
				startGameIfAllReady();
			}
		}
	}

	private void startGameIfAllReady() {
		for (PlayerLobbySession session : playerSessions) {
			if (!session.isReady()) {
				return;
			}
		}
		startGame();
	}

	private void startGame() {
		World world = World.createServerWorld(selectedContentPack, selectedGameModeId);
		GameSession session = new GameSession(world);
		CreateWorld createWorld = new CreateWorld();
		createWorld.setId(world.getId());
		createWorld.setContentPackIdentifier(new ContentPackIdentifier(selectedContentPack.getIdentifier()));
		createWorld.setGameModeId(selectedGameModeId);
		TeamComposition[] teamCompositions = new TeamComposition[teams.size()];
		int i = 0;
		for (Entry<String, Set<PlayerLobbySession>> lobbyTeam : teams.entrySet()) {
			PlayerInformation[] playerInformations = new PlayerInformation[lobbyTeam.getValue().size()];
			int j = 0;
			Team team = world.getTeamSet().createTeam(lobbyTeam.getKey());
			for (PlayerLobbySession p : lobbyTeam.getValue()) {
				PlayerEntity playerEntity = new PlayerEntity();
				world.getEntityPool().add(playerEntity);
				world.getPlayerEntities().put(playerEntity.getId(), playerEntity);
				world.getEntityPool().flushNewEntities();
				playerEntity.setTeam(team);
				playerEntity.setName(p.getConnection().toString());
				session.createPlayerSession(p.getConnection(), playerEntity);
				playerInformations[j++] = new PlayerInformation(playerEntity.getId(), playerEntity.getName());
			}
			teamCompositions[i] = new TeamComposition(team.getName(), playerInformations);
		}

		session.setTeamCompositions(teamCompositions);
		createWorld.setTeamCompositions(teamCompositions);

		world.initializeGame();

		server.addListener(session);
		terminate();

		session.foreachPlayers(playerSession -> {
			PlayerEntity playerEntity = playerSession.getPlayerEntity();
			createWorld.setMyPlayerId(playerEntity.getId());
			createWorld.setMyTeamId(playerEntity.getTeam().getId());
			createWorld.setMyPosition(playerEntity.getPosition());
			createWorld.setInventory(playerEntity.getInventory());
			playerSession.getConnection().sendTCP(createWorld);
		});

		session.foreachPlayers(playerSession -> {
			boolean messageSent = false;
			while (!messageSent) {
				if (playerSession.isGameReady()) {
					messageSent = true;
				} else {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

		playerSessions.forEach(s -> s.getConnection().sendTCP(new StartGame()));
		server.runGame(session);
	}

	private void terminate() {
		playerSessions.forEach(s -> s.getConnection().removePlayerConnectionMessageListeners(s));
		server.removeLobbySession(this);
	}

	private void setAllNotReady() {
		playerSessions.forEach(s -> s.setReady(false));
	}

	private void playerListChanged() {
		LobbyTeam[] lobbyTeams = new LobbyTeam[teams.size()];
		int i = 0;
		for (Entry<String, Set<PlayerLobbySession>> team : teams.entrySet()) {
			Set<PlayerLobbySession> players = team.getValue();
			LobbyPlayer[] lobbyPlayers = new LobbyPlayer[players.size()];
			int j = 0;
			for (PlayerLobbySession player : players) {
				LobbyPlayer lobbyPlayer = new LobbyPlayer();
				lobbyPlayer.setPlayerName(player.getConnection().toString());
				lobbyPlayer.setReady(player.isReady());
				lobbyPlayers[j++] = lobbyPlayer;

			}
			LobbyTeam lobbyTeam = new LobbyTeam(team.getKey(), lobbyPlayers);
			lobbyTeams[i++] = lobbyTeam;
		}
		sendToAll(new LobbyList(lobbyTeams));
	}

	private void sendToAll(Object message) {
		playerSessions.forEach(s -> s.getConnection().sendTCP(message));
	}
}
