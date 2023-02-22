package com.pixurvival.server;

import com.pixurvival.core.EndGameData;
import com.pixurvival.server.lobby.LobbySession;

public interface ServerGameListener {

    void lobbyStarted(LobbySession lobbySession);

    void gameStarted(GameSession gameSession);

    void gameEnded(EndGameData data);

    void playerLoggedIn(PlayerConnection playerConnection);

    void playerRejoined(PlayerConnection playerConnection);

    void disconnected(PlayerConnection playerConnection);
}
