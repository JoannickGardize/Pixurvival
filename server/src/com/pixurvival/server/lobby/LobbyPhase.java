package com.pixurvival.server.lobby;

import com.pixurvival.core.message.lobby.LobbyMessage;

public interface LobbyPhase {

    void started();

    void playerEntered(PlayerLobbySession playerSession);

    void playerLeaved(PlayerLobbySession playerSession);

    void received(PlayerLobbySession playerSession, LobbyMessage lobbyMessage);
}
