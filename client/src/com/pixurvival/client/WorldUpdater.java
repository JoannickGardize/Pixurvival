package com.pixurvival.client;

import com.pixurvival.core.util.Plugin;

public class WorldUpdater implements Plugin<PixurvivalClient> {
    @Override
    public void update(PixurvivalClient client) {
        client.updateWorld();
    }
}
