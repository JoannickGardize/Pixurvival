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
import com.pixurvival.core.map.HarvestableMapStructure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.TiledMapListener;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkPosition;

public class DefaultSoundsPlayer implements TiledMapListener, EntityPoolListener {

	private World world;

	public DefaultSoundsPlayer(World world) {
		this.world = world;
		world.getMap().addListener(this);
		world.getEntityPool().addListener(this);
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
	public void structureChanged(MapStructure mapStructure) {
		if (mapStructure instanceof HarvestableMapStructure) {
			HarvestableMapStructure hms = (HarvestableMapStructure) mapStructure;
			if (hms.isHarvested()) {
				playSound(world.getMyPlayer(), new SoundEffect(SoundPreset.SCRUNCH, hms.getPosition()));
			} else {
				playSound(world.getMyPlayer(), new SoundEffect(SoundPreset.POP, hms.getPosition()));
			}
		}
	}

	@Override
	public void structureAdded(MapStructure mapStructure) {
	}

	@Override
	public void structureRemoved(MapStructure mapStructure) {
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
			playSound(world.getMyPlayer(), new SoundEffect(SoundPreset.POP, e.getPosition()));
		}
	}

	private void playSound(PlayerEntity myPlayer, SoundEffect soundEffect) {
		float distanceSquared = myPlayer.distanceSquared(soundEffect.getPosition());
		if (distanceSquared <= GameConstants.PLAYER_VIEW_DISTANCE * GameConstants.PLAYER_VIEW_DISTANCE) {
			Sound sound = PixurvivalGame.getInstance().getSound(soundEffect.getPreset());
			float volume = 1f - 0.8f * distanceSquared / (GameConstants.PLAYER_VIEW_DISTANCE * GameConstants.PLAYER_VIEW_DISTANCE);
			float pan = 0.1f + 0.9f * (soundEffect.getPosition().getX() - myPlayer.getPosition().getX()) / GameConstants.PLAYER_VIEW_DISTANCE;
			sound.play(volume * PixurvivalGame.getInstance().getGlobalVolume(), 1f, pan);
		}
	}
}
