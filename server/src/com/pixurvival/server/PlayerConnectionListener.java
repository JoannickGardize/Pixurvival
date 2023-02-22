package com.pixurvival.server;

import com.pixurvival.core.message.ClientStream;
import com.pixurvival.core.message.GameReady;
import com.pixurvival.core.message.RefreshRequest;
import com.pixurvival.core.message.lobby.LobbyMessage;
import com.pixurvival.core.message.playerRequest.IPlayerActionRequest;

public interface PlayerConnectionListener {

    default void handleGameReady(GameReady gameReady) {
    }

    default void handleRefreshRequest(RefreshRequest refreshRequest) {
    }

    default void handlePlayerActionRequest(IPlayerActionRequest playerActionRequest) {
    }

    default void handleClientStream(ClientStream clientStream) {
    }

    default void handleLobbyMessage(LobbyMessage message) {
    }

    default void handleDisconnected() {
    }
}
