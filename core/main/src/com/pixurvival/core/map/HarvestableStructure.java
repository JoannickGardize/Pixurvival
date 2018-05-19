package com.pixurvival.core.map;

import java.nio.ByteBuffer;
import java.util.Random;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.Entity;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.message.HarvestableStructureUpdate;
import com.pixurvival.core.message.StructureUpdate;

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
		getChunk().updateTimestamp();
		World world = getChunk().getMap().getWorld();
		world.getActionTimerManager().addActionTimer(() -> {
			harvested = false;
			getChunk().getMap().notifyListeners(l -> l.structureChanged(this));
			getChunk().updateTimestamp();
		}, getDefinition().getRespawnTime().next(world.getRandom()));
		return getDefinition().getItemReward().produce(random);
	}

	@Override
	public boolean canInteract(Entity entity) {
		return !harvested && super.canInteract(entity);
	}

	@Override
	public void writeData(ByteBuffer buffer) {
		buffer.put(harvested ? (byte) 1 : (byte) 0);
	}

	@Override
	public void applyData(ByteBuffer buffer) {
		harvested = buffer.get() == 1;
	}

	@Override
	public StructureUpdate getUpdate() {
		return new HarvestableStructureUpdate(getTileX(), getTileY(), harvested);
	}

}
