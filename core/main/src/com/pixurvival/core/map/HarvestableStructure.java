package com.pixurvival.core.map;

import java.util.Random;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.ActionTimer;
import com.pixurvival.core.Entity;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.item.ItemStack;

import lombok.Getter;
import lombok.Setter;

@Getter
public class HarvestableStructure extends MapStructure {

	public HarvestableStructure(Chunk chunk, Structure definition, int x, int y) {
		super(chunk, definition, x, y);
	}

	private @Setter boolean harvested = false;

	public ItemStack[] harvest(Random random) {
		if (harvested) {
			Log.warn("warning, trying to harvest already harvested structure at " + getX() + ", " + getY());
			return new ItemStack[0];
		}
		harvested = true;
		getChunk().getMap().notifyListeners(l -> l.structureChanged(this));
		World world = getChunk().getMap().getWorld();
		world.getActionTimerManager().add(new ActionTimer(() -> {
			harvested = false;
			getChunk().getMap().notifyListeners(l -> l.structureChanged(this));
		}, world.getTime().getTime() + getDefinition().getRespawnTime().next(world.getRandom())));
		return getDefinition().getItemReward().produce(random);
	}

	@Override
	public boolean canInteract(Entity entity) {
		return !harvested && super.canInteract(entity);
	}

	@Override
	public byte getData() {
		return (byte) (harvested ? 1 : 0);
	}

	@Override
	public void applyData(byte data) {
		harvested = data == 1;
	}
}
