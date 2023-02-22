package com.pixurvival.core.entity;

import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.WorldUpdate;

public interface EntityPoolListener {

    void entityAdded(Entity e);

    void entityRemoved(Entity e);

    /**
     * A sneaky remove is a remove that both the client and the server knows about,
     * and thus, does not need to be sent via a {@link WorldUpdate}.
     *
     * @param e
     */
    void sneakyEntityRemoved(Entity e);

    /**
     * @param player
     * @param respawnTime the world time at which the player will respawn, or -1 if no
     *                    respawn is planned.
     */
    void playerDied(PlayerEntity player);

    void playerRespawned(PlayerEntity player);
}
