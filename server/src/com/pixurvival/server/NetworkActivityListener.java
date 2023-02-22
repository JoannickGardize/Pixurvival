package com.pixurvival.server;

public interface NetworkActivityListener {

    void sent(PlayerGameSession session, Object object, int size);
}
