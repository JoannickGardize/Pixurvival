package com.pixurvival.server.lobby;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.TranslationKey;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.message.lobby.ChangeTeamRequest;
import com.pixurvival.core.message.lobby.ChooseGameModeRequest;
import com.pixurvival.core.message.lobby.CreateTeamRequest;
import com.pixurvival.core.message.lobby.EnterLobby;
import com.pixurvival.core.message.lobby.GameModeList;
import com.pixurvival.core.message.lobby.GameModeListRequest;
import com.pixurvival.core.message.lobby.LobbyData;
import com.pixurvival.core.message.lobby.LobbyMessage;
import com.pixurvival.core.message.lobby.LobbyPlayer;
import com.pixurvival.core.message.lobby.LobbyTeam;
import com.pixurvival.core.message.lobby.ReadyRequest;
import com.pixurvival.core.message.lobby.RemoveTeamRequest;
import com.pixurvival.core.message.lobby.RenameTeamRequest;
import com.pixurvival.core.util.Cache;
import com.pixurvival.core.util.LocaleUtils;

public class PreparingPhase implements LobbyPhase {

	private int modCount;
	private ContentPackIdentifier[] availableContentPacks;
	private int selectedContentPackIndex;
	private int selectedGameModeIndex;
	private LobbySession session;
	/**
	 * ContentPacks must be pre-loaded to get the GameMode list.
	 */
	private Cache<ContentPackIdentifier, ContentPack> contentPackCache = new Cache<>(identifier -> {
		try {
			return session.getServer().getContentPackSerialization().load(identifier, false);
		} catch (ContentPackException e) {
			e.printStackTrace();
			return null;
		}
	});

	public PreparingPhase(LobbySession session) {
		this.session = session;
		List<ContentPackIdentifier> list = session.getServer().getContentPackSerialization().list();
		availableContentPacks = list.toArray(new ContentPackIdentifier[list.size()]);
		if (availableContentPacks.length == 0) {
			throw new IllegalStateException("No ContentPack available at " + session.getServer().getContentPackSerialization().getWorkingDirectory());
		}
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
		} else if (lobbyMessage instanceof GameModeListRequest) {
			sendGameModeList(playerSession, (GameModeListRequest) lobbyMessage);
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
			if (gameModeIndex >= 0 && gameModeIndex < contentPackCache.get(availableContentPacks[selectedContentPackIndex]).getGameModes().size()) {
				selectedGameModeIndex = gameModeIndex;
			} else {
				selectedGameModeIndex = 0;
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
		for (PlayerLobbySession playerSession : session.getPlayerSessions()) {
			if (!playerSession.getLobbyPlayer().isReady()) {
				return;
			}
		}
		StartingGameData data = new StartingGameData();
		data.setContentPack(contentPackCache.get(availableContentPacks[selectedContentPackIndex]));
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
		GameMode gm = contentPackCache.get(availableContentPacks[selectedContentPackIndex]).getGameModes().get(selectedGameModeIndex);
		message.setMaxPlayer(gm.getTeamNumberInterval().getMax() * gm.getTeamSizeInterval().getMax());
		session.getPlayerSessions().forEach(s -> {
			message.setMyPlayer(s.getLobbyPlayer());
			message.setMyTeamName(s.getTeamName());
			s.getConnection().sendTCP(message);
		});
	}

	private void sendGameModeList(PlayerLobbySession session, GameModeListRequest request) {
		int packIndex = request.getContentPackIndex();
		if (packIndex < 0 || packIndex >= availableContentPacks.length) {
			return;
		}
		ContentPack pack = contentPackCache.get(availableContentPacks[packIndex]);
		Locale bestLocale = LocaleUtils.findBestMatch(Arrays.asList(request.getRequestedLocales()), pack.getTranslations().keySet());
		String[] gameModeList = new String[pack.getGameModes().size()];
		for (int i = 0; i < gameModeList.length; i++) {
			gameModeList[i] = pack.getTranslation(bestLocale, pack.getGameModes().get(i), TranslationKey.NAME);
		}
		session.getConnection().sendTCP(new GameModeList(pack.getIdentifier(), gameModeList));
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
