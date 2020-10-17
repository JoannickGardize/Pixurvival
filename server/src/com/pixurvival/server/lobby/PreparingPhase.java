package com.pixurvival.server.lobby;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.summary.ContentPackSummary;
import com.pixurvival.core.message.lobby.ChangeTeamRequest;
import com.pixurvival.core.message.lobby.ChooseGameModeRequest;
import com.pixurvival.core.message.lobby.CreateTeamRequest;
import com.pixurvival.core.message.lobby.EnterLobby;
import com.pixurvival.core.message.lobby.LobbyData;
import com.pixurvival.core.message.lobby.LobbyMessage;
import com.pixurvival.core.message.lobby.LobbyPlayer;
import com.pixurvival.core.message.lobby.LobbyTeam;
import com.pixurvival.core.message.lobby.ReadyRequest;
import com.pixurvival.core.message.lobby.RemoveTeamRequest;
import com.pixurvival.core.message.lobby.RenameTeamRequest;

public class PreparingPhase implements LobbyPhase {

	private int modCount;
	private ContentPackSummary[] availableContentPacks;
	private int selectedContentPackIndex;
	private int selectedGameModeIndex;
	private LobbySession session;

	public PreparingPhase(LobbySession session) {
		this.session = session;
		List<ContentPackSummary> list = session.getServer().getContentPackContext().list();
		availableContentPacks = list.toArray(new ContentPackSummary[list.size()]);
		if (availableContentPacks.length == 0) {
			throw new IllegalStateException("No ContentPack available at " + session.getServer().getContentPackContext().getWorkingDirectory());
		}
		chooseGameMode(new ChooseGameModeRequest(0, 0));
	}

	@Override
	public void started() {
	}

	@Override
	public void playerEntered(PlayerLobbySession playerSession) {
		changeTeam(playerSession, session.getTeams().get(0));
		setAllNotReady();
		playerSession.getConnection().sendTCP(new EnterLobby());
		dataChanged();
	}

	@Override
	public void playerLeaved(PlayerLobbySession playerSession) {
		Optional.ofNullable(getTeamByName(playerSession.getTeamName())).ifPresent(t -> t.getMembers().remove(playerSession));
		setAllNotReady();
		dataChanged();
	}

	@Override
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
		} else if (lobbyMessage instanceof ChooseGameModeRequest) {
			chooseGameMode((ChooseGameModeRequest) lobbyMessage);
		}
	}

	public void abordStartingGame() {
		setAllNotReady();
		dataChanged();
	}

	private void chooseGameMode(ChooseGameModeRequest request) {
		int contentPackIndex = request.getContentPackIndex();
		if (contentPackIndex >= 0 && contentPackIndex < availableContentPacks.length) {
			selectedContentPackIndex = contentPackIndex;
			int gameModeIndex = request.getGameModeIndex();
			if (gameModeIndex >= 0 && gameModeIndex < availableContentPacks[contentPackIndex].getGameModeSummaries().length) {
				selectedGameModeIndex = gameModeIndex;
			} else {
				selectedGameModeIndex = -1;
			}
			setAllNotReady();
			dataChanged();
		}
	}

	private void createTeam(String teamName) {
		if (isTeamNameValid(teamName)) {
			session.getTeams().add(new LobbySessionTeam(teamName));
			dataChanged();
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
			dataChanged();
		}
	}

	private void removeTeam(String teamName) {
		if (session.getTeams().size() > 1) {
			LobbySessionTeam team = removeTeamByName(teamName);
			if (team == null) {
				return;
			}
			LobbySessionTeam newTeam = session.getTeams().get(0);
			if (!team.getMembers().isEmpty()) {
				team.getMembers().forEach(p -> {
					p.setTeamName(newTeam.getName());
					newTeam.getMembers().add(p);
				});
			}
			setAllNotReady();
			dataChanged();
		}
	}

	private void changeTeam(PlayerLobbySession playerSession, LobbySessionTeam newTeam) {
		String oldTeamName = playerSession.getTeamName();
		if (!Objects.equals(newTeam.getName(), oldTeamName)) {
			playerSession.setTeamName(newTeam.getName());
			Optional.ofNullable(getTeamByName(oldTeamName)).ifPresent(t -> t.getMembers().remove(playerSession));
			newTeam.getMembers().add(playerSession);
			setAllNotReady();
			dataChanged();
		}
	}

	private void changeTeam(PlayerLobbySession playerSession, String newTeamName) {
		changeTeam(playerSession, getTeamByName(newTeamName));
	}

	private void changeReady(PlayerLobbySession playerSession, boolean ready, int modCount) {
		if (playerSession.getLobbyPlayer().isReady() != ready && (!ready || this.modCount == modCount)) {
			playerSession.getLobbyPlayer().setReady(ready);
			dataChanged();
			if (ready) {
				startGameIfAllReady();
			}
		}
	}

	private void setAllNotReady() {
		modCount++;
		session.getPlayerSessions().forEach(s -> s.getLobbyPlayer().setReady(false));
	}

	private void startGameIfAllReady() {
		if (selectedGameModeIndex == -1) {
			return;
		}
		for (PlayerLobbySession playerSession : session.getPlayerSessions()) {
			if (!playerSession.getLobbyPlayer().isReady()) {
				return;
			}
		}
		StartingGameData data = new StartingGameData();
		try {
			data.setContentPack(session.getServer().getContentPackContext().load(availableContentPacks[selectedContentPackIndex].getIdentifier()));
		} catch (ContentPackException e) {
			Log.error("Unable to load the content pack " + availableContentPacks[selectedContentPackIndex].getIdentifier(), e);
			return;
		}
		data.setGameModeId(selectedGameModeIndex);
		session.getPhase(ContentPackCheckPhase.class).setData(data);
		session.setCurrentPhase(ContentPackCheckPhase.class);

	}

	private void dataChanged() {
		dataChanged(true);
	}

	private void dataChanged(boolean playerTeamsChanged) {
		LobbyData message = new LobbyData();
		if (playerTeamsChanged) {
			LobbyTeam[] lobbyTeams = new LobbyTeam[session.getTeams().size()];
			for (int i = 0; i < session.getTeams().size(); i++) {
				LobbySessionTeam team = session.getTeams().get(i);
				List<PlayerLobbySession> players = team.getMembers();
				LobbyPlayer[] lobbyPlayers = new LobbyPlayer[players.size()];
				for (int j = 0; j < players.size(); j++) {
					PlayerLobbySession player = players.get(j);
					lobbyPlayers[j] = player.getLobbyPlayer();

				}
				LobbyTeam lobbyTeam = new LobbyTeam(team.getName(), lobbyPlayers);
				lobbyTeams[i] = lobbyTeam;
			}
			message.setPlayers(lobbyTeams);
		}
		message.setModCount(modCount);
		message.setAvailableContentPacks(availableContentPacks);
		message.setSelectedContentPackIndex(selectedContentPackIndex);
		message.setSelectedGameModeIndex(selectedGameModeIndex);
		session.getPlayerSessions().forEach(s -> {
			message.setMyPlayer(s.getLobbyPlayer());
			message.setMyTeamName(s.getTeamName());
			s.getConnection().sendTCP(message);
		});
	}

	private LobbySessionTeam getTeamByName(String name) {
		for (LobbySessionTeam team : session.getTeams()) {
			if (team.getName().equals(name)) {
				return team;
			}
		}
		return null;
	}

	private LobbySessionTeam removeTeamByName(String name) {
		for (int i = 0; i < session.getTeams().size(); i++) {
			if (session.getTeams().get(i).getName().equals(name)) {
				return session.getTeams().remove(i);
			}
		}
		return null;
	}
}
