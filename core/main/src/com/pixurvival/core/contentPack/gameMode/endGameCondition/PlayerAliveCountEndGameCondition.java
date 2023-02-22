package com.pixurvival.core.contentPack.gameMode.endGameCondition;

import com.pixurvival.core.World;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityPoolListener;
import com.pixurvival.core.livingEntity.PlayerEntity;

public abstract class PlayerAliveCountEndGameCondition extends EndGameCondition implements EntityPoolListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void initialize(World world) {
        world.getEntityPool().addListener(this);
    }

    @Override
    public void initializeNewGameData(World world) {
        setGameData(world, false);
    }

    @Override
    public boolean update(World world) {
        return (boolean) getGameData(world);
    }

    @Override
    public void entityAdded(Entity e) {
        // Nothing
    }

    @Override
    public void entityRemoved(Entity e) {
        // Nothing
    }

    @Override
    public void sneakyEntityRemoved(Entity e) {
        // Nothing
    }

    @Override
    public void playerDied(PlayerEntity player) {
        setGameData(player.getWorld(), compute(player.getWorld()));
    }

    @Override
    public void playerRespawned(PlayerEntity player) {
        setGameData(player.getWorld(), compute(player.getWorld()));
    }

    protected abstract boolean compute(World world);
}