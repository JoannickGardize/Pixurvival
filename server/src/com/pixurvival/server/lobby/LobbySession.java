package com.pixurvival.server.lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.Version;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.PlayerInformation;
import com.pixurvival.core.message.StartGame;
import com.pixurvival.core.message.TeamComposition;
import com.pixurvival.core.message.lobby.ChangeTeamRequest;
import com.pixurvival.core.message.lobby.CreateTeamRequest;
import com.pixurvival.core.message.lobby.EnterLobby;
import com.pixurvival.core.message.lobby.LobbyData;
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
	private ContentPackIdentifier selectedContentPackIdentifier = new ContentPackIdentifier("Vanilla", new Version("1.0"));
	private int selectedGameModeId;
	private List<LobbySessionTeam> teams = new ArrayList<>();
	private PixurvivalServer server;
	private int gameReadyCount = 0;
	private GameSession waitingGameSession;
	private int modCount;

	public LobbySession(PixurvivalServer server) {
		this.server = server;
		teams.add(new LobbySessionTeam(DEFAULT_TEAM_NAME));
	}

	public void addPlayer(PlayerConnection connection) {
		PlayerLobbySession playerSession = new PlayerLobbySession(this, connection);
		connection.addPlayerConnectionMessageListeners(playerSession);
		playerSessions.add(playerSession);
		changeTeam(playerSession, teams.get(0));
		setAllNotReady();
		playerSession.getConnection().sendTCP(new EnterLobby());
		playerListChanged();
	}

	public void removePlayer(PlayerLobbySession session) {
		playerSessions.remove(session);
		Optional.ofNullable(getTeamByName(session.getTeamName())).ifPresent(t -> t.getMembers().remove(session));
		setAllNotReady();
		playerListChanged();
	}

	public void received(PlayerLobbySession playerSession, LobbyMessage lobbyMessage) {
		if (lobbyMessage instanceof ChangeTeamRequest) {
			changeTeam(playerSession, ((ChangeTeamRequest) lobbyMessage).getTeamName());
		} else if (lobbyMessage instanceof ReadyRequest) {
			ReadyRequest readyRequest = (ReadyRequest) lobbyMessage;
			changeReady(playerSession, readyRequest.isReady(), readyRequest.getModCount());
		} else if (lobbyMessage instanceof CreateTeamRequest) {
			createTeam(((CreateTeamRequest) lobbyMessage).getTeamName().trim());
		} else if (lobbyMessage instanceof RenameTeamRequest) {
			RenameTeamRequest renameTeamRequest = (RenameTeamRequest) lobbyMessage;
			renameTeam(renameTeamRequest.getOldName(), renameTeamRequest.getNewName().trim());
		} else if (lobbyMessage instanceof RemoveTeamRequest) {
			removeTeam(((RemoveTeamRequest) lobbyMessage).getTeamName());
		}
	}

	public void receivedGameReady() {
		if (waitingGameSession != null) {
			gameReadyCount++;
			if (gameReadyCount == playerSessions.size()) {
				terminate();
				waitingGameSession.foreachPlayers(s -> s.getConnection().sendTCP(new StartGame()));
				server.runGame(waitingGameSession);
			}
		}
	}

	private void createTeam(String teamName) {
		if (isTeamNameValid(teamName)) {
			teams.add(new LobbySessionTeam(teamName));
			playerListChanged();
		}
	}

	private boolean isTeamNameValid(String teamName) {
		return teamName != null && teamName.length() > 0 && getTeamByName(teamName) == null;
	}

	private void renameTeam(String oldName, String newName) {
		LobbySessionTeam team = getTeamByName(oldName);
		if (team != null && isTeamNameValid(newName)) {
			team.setName(newName);
			team.getMembers().forEach(p -> p.setTeamName(newName));
			playerListChanged();
		}
	}

	private void removeTeam(String teamName) {
		if (teams.size() > 1) {
			LobbySessionTeam team = removeTeamByName(teamName);
			if (team == null) {
				return;
			}
			LobbySessionTeam newTeam = teams.get(0);
			if (!team.getMembers().isEmpty()) {
				team.getMembers().forEach(p -> {
					p.setTeamName(newTeam.getName());
					newTeam.getMembers().add(p);
				});
				setAllNotReady();
				playerListChanged();
			}
		}
	}

	private void changeTeam(PlayerLobbySession playerSession, LobbySessionTeam newTeam) {
		String oldTeamName = playerSession.getTeamName();
		if (!Objects.equals(newTeam.getName(), oldTeamName) && newTeam != null) {
			playerSession.setTeamName(newTeam.getName());
			Optional.ofNullable(getTeamByName(oldTeamName)).ifPresent(t -> t.getMembers().remove(playerSession));
			newTeam.getMembers().add(playerSession);
			setAllNotReady();
			playerListChanged();
		}
	}

	private void changeTeam(PlayerLobbySession playerSession, String newTeamName) {
		changeTeam(playerSession, getTeamByName(newTeamName));
	}

	private void changeReady(PlayerLobbySession playerSession, boolean ready, int modCount) {
		if (playerSession.getLobbyPlayer().isReady() != ready && (!ready || this.modCount == modCount)) {
			playerSession.getLobbyPlayer().setReady(ready);
			playerListChanged();
			if (ready) {
				startGameIfAllReady();
			}
		}
	}

	private void startGameIfAllReady() {
		for (PlayerLobbySession session : playerSessions) {
			if (!session.getLobbyPlayer().isReady()) {
				return;
			}
		}
		startGame();
	}

	private void startGame() {
		ContentPack contentPack;
		try {
			contentPack = server.getContentPackSerializer().load(selectedContentPackIdentifier);
		} catch (ContentPackException e1) {
			e1.printStackTrace();
			return;
		}
		World world = World.createServerWorld(contentPack, selectedGameModeId);
		waitingGameSession = new GameSession(world);
		CreateWorld createWorld = new CreateWorld();
		createWorld.setId(world.getId());
		createWorld.setContentPackIdentifier(selectedContentPackIdentifier);
		createWorld.setGameModeId(selectedGameModeId);
		List<TeamComposition> teamCompositionList = new ArrayList<>();
		for (int i = 0; i < teams.size(); i++) {
			LobbySessionTeam lobbyTeam = teams.get(i);
			List<PlayerLobbySession> members = lobbyTeam.getMembers();
			if (members.isEmpty()) {
				continue;
			}
			PlayerInformation[] playerInformations = new PlayerInformation[members.size()];
			Team team = world.getTeamSet().createTeam(lobbyTeam.getName());
			for (int j = 0; j < members.size(); j++) {
				PlayerLobbySession p = members.get(j);
				PlayerEntity playerEntity = new PlayerEntity();
				world.getEntityPool().add(playerEntity);
				world.getPlayerEntities().put(playerEntity.getId(), playerEntity);
				world.getEntityPool().flushNewEntities();
				playerEntity.setTeam(team);
				playerEntity.setName(p.getConnection().toString());
				waitingGameSession.createPlayerSession(p.getConnection(), playerEntity);
				playerInformations[j] = new PlayerInformation(playerEntity.getId(), playerEntity.getName());
			}
			teamCompositionList.add(new TeamComposition(team.getName(), playerInformations));
		}
		TeamComposition[] teamCompositions = teamCompositionList.toArray(new TeamComposition[teamCompositionList.size()]);

		waitingGameSession.setTeamCompositions(teamCompositions);
		createWorld.setTeamCompositions(teamCompositions);

		world.initializeGame();
		server.addListener(waitingGameSession);

		waitingGameSession.foreachPlayers(playerSession -> {
			PlayerEntity playerEntity = playerSession.getPlayerEntity();
			createWorld.setMyPlayerId(playerEntity.getId());
			createWorld.setMyTeamId(playerEntity.getTeam().getId());
			createWorld.setMyPosition(playerEntity.getPosition());
			createWorld.setInventory(playerEntity.getInventory());
			playerSession.getConnection().sendTCP(createWorld);
		});
	}

	private void terminate() {
		playerSessions.forEach(s -> s.getConnection().removePlayerConnectionMessageListeners(s));
		server.removeLobbySession(this);
	}

	private void setAllNotReady() {
		modCount++;
		playerSessions.forEach(s -> s.getLobbyPlayer().setReady(false));
	}

	private void playerListChanged() {
		LobbyTeam[] lobbyTeams = new LobbyTeam[teams.size()];
		for (int i = 0; i < teams.size(); i++) {
			LobbySessionTeam team = teams.get(i);
			List<PlayerLobbySession> players = team.getMembers();
			LobbyPlayer[] lobbyPlayers = new LobbyPlayer[players.size()];
			for (int j = 0; j < players.size(); j++) {
				PlayerLobbySession player = players.get(j);
				lobbyPlayers[j] = player.getLobbyPlayer();

			}
			LobbyTeam lobbyTeam = new LobbyTeam(team.getName(), lobbyPlayers);
			lobbyTeams[i] = lobbyTeam;
		}
		LobbyData message = new LobbyData();
		message.setPlayers(lobbyTeams);
		message.setModCount(modCount);
		playerSessions.forEach(s -> {
			message.setMyPlayer(s.getLobbyPlayer());
			message.setMyTeamName(s.getTeamName());
			s.getConnection().sendTCP(message);
		});
	}

	private LobbySessionTeam getTeamByName(String name) {
		for (LobbySessionTeam team : teams) {
			if (team.getName().equals(name)) {
				return team;
			}
		}
		return null;
	}

	private LobbySessionTeam removeTeamByName(String name) {
		for (int i = 0; i < teams.size(); i++) {
			if (teams.get(i).getName().equals(name)) {
				return teams.remove(i);
			}
		}
		return null;
	}
}
