package com.pixurvival.server;

public interface NetworkActivityListener {

	void sent(PlayerConnection connection, Object object, int size);
}
