package com.pixurvival.gdxcore;

import com.badlogic.gdx.audio.Sound;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.SoundEffect;
import com.pixurvival.core.SoundPreset;
import com.pixurvival.core.World;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityPoolListener;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.item.ItemStackEntity.State;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.HarvestableStructureEntity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.TiledMapListener;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkPosition;
import com.pixurvival.core.map.chunk.update.StructureUpdate;

public class DefaultSoundsPlayer implements TiledMapListener, EntityPoolListener {

    public static final long MIN_DELAY_SAME_SOUND = 200;

    private World world;
    private long[] lastPlayedTimes;

    public DefaultSoundsPlayer(World world) {
        this.world = world;
        world.getMap().addListener(this);
        world.getEntityPool().addListener(this);
        lastPlayedTimes = new long[SoundPreset.values().length + world.getContentPack().getSoundIdByName().size()];
    }

    public void playSounds() {
        PlayerEntity myPlayer = world.getMyPlayer();
        for (SoundEffect soundEffect : myPlayer.getSoundEffectsToConsume()) {
            playSound(myPlayer, soundEffect);
        }
        myPlayer.getSoundEffectsToConsume().clear();
    }

    @Override
    public void chunkLoaded(Chunk chunk) {
    }

    @Override
    public void chunkUnloaded(Chunk chunk) {
    }

    @Override
    public void structureChanged(StructureEntity mapStructure, StructureUpdate structureUpdate) {
        if (mapStructure instanceof HarvestableStructureEntity) {
            HarvestableStructureEntity hms = (HarvestableStructureEntity) mapStructure;
            if (hms.isHarvested()) {
                playSound(world.getMyPlayer(), new SoundEffect(SoundPreset.SCRUNCH.ordinal(), hms.getPosition()));
            } else {
                playSound(world.getMyPlayer(), new SoundEffect(SoundPreset.POP.ordinal(), hms.getPosition()));
            }
        }
    }

    @Override
    public void structureAdded(StructureEntity mapStructure) {
    }

    @Override
    public void structureRemoved(StructureEntity mapStructure) {
        playSound(world.getMyPlayer(), new SoundEffect(SoundPreset.SCRUNCH.ordinal(), mapStructure.getPosition()));
    }

    @Override
    public void entityEnterChunk(ChunkPosition previousPosition, Entity e) {
    }

    @Override
    public void entityAdded(Entity e) {
    }

    @Override
    public void entityRemoved(Entity e) {
    }

    @Override
    public void sneakyEntityRemoved(Entity e) {
        if (e instanceof ItemStackEntity && ((ItemStackEntity) e).getState() == State.MAGNTIZED) {
            playSound(world.getMyPlayer(), new SoundEffect(SoundPreset.POP.ordinal(), e.getPosition()));
        }
    }

    private void playSound(PlayerEntity myPlayer, SoundEffect soundEffect) {
        float distanceSquared = myPlayer.distanceSquared(soundEffect.getPosition());
        if (distanceSquared <= GameConstants.PLAYER_VIEW_DISTANCE * GameConstants.PLAYER_VIEW_DISTANCE) {
            long time = System.currentTimeMillis();
            if (time - lastPlayedTimes[soundEffect.getId()] > MIN_DELAY_SAME_SOUND) {
                lastPlayedTimes[soundEffect.getId()] = time;
                Sound sound = PixurvivalGame.getInstance().getSound(soundEffect);
                float volume = 1f - 0.9f * distanceSquared / (GameConstants.PLAYER_VIEW_DISTANCE * GameConstants.PLAYER_VIEW_DISTANCE);
                float pan = 0.9f * (soundEffect.getPosition().getX() - myPlayer.getPosition().getX()) / GameConstants.PLAYER_VIEW_DISTANCE;
                sound.play(volume * PixurvivalGame.getInstance().getGlobalVolume(), 1f, pan);
            }
        }
    }

    @Override
    public void playerDied(PlayerEntity player) {
    }

    @Override
    public void playerRespawned(PlayerEntity player) {
    }
}
