package com.pixurvival.server;

import com.esotericsoftware.kryonet.Connection;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlayerConnection extends Connection {

    private @Getter List<PlayerConnectionListener> playerConnectionListeners = new ArrayList<>();
    private boolean logged = false;

    /**
     * Create new list to solve ConcurentModificationException
     *
     * @param playerConnectionMessageListener
     */
    public void addPlayerConnectionMessageListeners(PlayerConnectionListener playerConnectionMessageListener) {
        List<PlayerConnectionListener> newList = new ArrayList<>();
        newList.addAll(playerConnectionListeners);
        newList.add(playerConnectionMessageListener);
        playerConnectionListeners = newList;
    }

    /**
     * Create new list to solve ConcurentModificationException
     *
     * @param playerConnectionMessageListener
     */
    public void removePlayerConnectionMessageListeners(PlayerConnectionListener playerConnectionMessageListener) {
        List<PlayerConnectionListener> newList = new ArrayList<>();
        newList.addAll(playerConnectionListeners);
        newList.remove(playerConnectionMessageListener);
        playerConnectionListeners = newList;
    }

    public void disconnected() {
        playerConnectionListeners.forEach(PlayerConnectionListener::handleDisconnected);
    }
}
