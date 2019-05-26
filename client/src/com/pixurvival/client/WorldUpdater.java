package com.pixurvival.client;

import com.pixurvival.core.util.Plugin;

public class WorldUpdater implements Plugin<ClientGame> {
	@Override
	public void update(ClientGame client) {
		client.updateWorld();
	}
}
