package com.pixurvival.server.lobby;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.contentPack.ContentPackException;
import com.pixurvival.core.contentPack.summary.ContentPackSummary;
import com.pixurvival.core.contentPack.summary.GameModeSummary;
import com.pixurvival.core.contentPack.summary.RoleSummary;
import com.pixurvival.core.message.lobby.*;

import java.util.*;

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
        } else if (lobbyMessage instanceof ChooseRoleRequest) {
            changeRole(playerSession, (ChooseRoleRequest) lobbyMessage);
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
            setAllLastRole();
            dataChanged();
        }
    }

    private void setAllLastRole() {
        GameModeSummary gameMode = getSelectedGameMode();
        if (gameMode.getRoleSummaries() != null) {
            int lastRole = gameMode.getRoleSummaries().length - 1;
            session.getPlayerSessions().forEach(s -> s.getLobbyPlayer().setSelectedRole(lastRole));
        }
    }

    private GameModeSummary getSelectedGameMode() {
        return getSelectedContentPack().getGameModeSummaries()[selectedGameModeIndex];
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

    private void changeRole(PlayerLobbySession playerSession, ChooseRoleRequest roleRequest) {
        if (roleRequest.getId() != playerSession.getLobbyPlayer().getSelectedRole()) {
            playerSession.getLobbyPlayer().setSelectedRole(roleRequest.getId());
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
        if (!checkQuantitiesOkToStart()) {
            return;
        }
        StartingGameData data = new StartingGameData();
        try {
            data.setContentPack(session.getServer().getContentPackContext().load(getSelectedContentPack().getIdentifier()));
        } catch (ContentPackException e) {
            Log.error("Unable to load the content pack " + getSelectedContentPack().getIdentifier(), e);
            setAllNotReady();
            dataChanged();
            return;
        }
        if (!session.getServer().getContentPackContext().getErrors(data.getContentPack()).isEmpty()) {
            Log.warn("the content pack " + getSelectedContentPack().getIdentifier() + " contains errors. Open it with the editor to solve them.");
            setAllNotReady();
            dataChanged();
            return;
        }
        data.setGameModeId(selectedGameModeIndex);
        session.getPhase(ContentPackCheckPhase.class).setData(data);
        session.setCurrentPhase(ContentPackCheckPhase.class);

    }

    private ContentPackSummary getSelectedContentPack() {
        return availableContentPacks[selectedContentPackIndex];
    }

    private boolean checkQuantitiesOkToStart() {
        GameModeSummary gameMode = getSelectedGameMode();
        if (!gameMode.getTeamNumberInterval().test(session.getTeams().size())) {
            session.getPlayerSessions().forEach(s -> s.getConnection().sendTCP(LobbyServerMessage.NUMBER_OF_TEAM));
            return false;
        }
        for (LobbySessionTeam team : session.getTeams()) {
            if (!gameMode.getTeamSizeInterval().test(team.getMembers().size())) {
                session.getPlayerSessions().forEach(s -> s.getConnection().sendTCP(LobbyServerMessage.NUMBER_OF_PLAYER));
                return false;
            }
        }
        return checkRolesOkToStart();
    }

    private boolean checkRolesOkToStart() {
        GameModeSummary gameMode = getSelectedGameMode();
        if (gameMode.getRoleSummaries() == null) {
            return true;
        }
        Map<Integer, Integer> roleCounts = new HashMap<>();
        for (LobbySessionTeam team : session.getTeams()) {
            roleCounts.clear();
            for (PlayerLobbySession player : team.getMembers()) {
                int roleId = player.getLobbyPlayer().getSelectedRole();
                roleCounts.put(roleId, roleCounts.getOrDefault(roleId, 0));
            }
            for (int i = 0; i < gameMode.getRoleSummaries().length; i++) {
                RoleSummary role = gameMode.getRoleSummaries()[i];
                int currentCount = roleCounts.getOrDefault(i, 0);
                if (currentCount < role.getMinimumPerTeam() || currentCount > role.getMaximumPerTeam()) {
                    session.getPlayerSessions().forEach(s -> s.getConnection().sendTCP(LobbyServerMessage.NUMBER_OF_ROLE));
                    return false;
                }
            }
        }
        return true;
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
