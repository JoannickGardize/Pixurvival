package com.pixurvival.core.map;

import java.nio.ByteBuffer;
import java.util.Random;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.structure.HarvestableStructure;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.update.HarvestableStructureUpdate;
import com.pixurvival.core.util.LongSequenceIOHelper;

import lombok.Getter;
import lombok.Setter;

@Getter
public class HarvestableMapStructure extends MapStructure {

	public HarvestableMapStructure(Chunk chunk, Structure definition, int x, int y) {
		super(chunk, definition, x, y);
	}

	private @Setter boolean harvested = false;

	public ItemStack[] harvest(Random random) {
		if (harvested) {
			Log.warn("warning, trying to harvest already harvested structure at " + getPosition().getX() + ", " + getPosition().getY());
			return new ItemStack[0];
		}
		harvested = true;
		getChunk().getMap().notifyListeners(l -> l.structureChanged(this, new HarvestableStructureUpdate(getTileX(), getTileY(), getId(), harvested)));
		getChunk().updateTimestamp();
		World world = getChunk().getMap().getWorld();
		world.getActionTimerManager().addActionTimer(new HarvestableStructureUpdate(getTileX(), getTileY(), getId(), false),
				((HarvestableStructure) getDefinition()).getRegrowthTime().next(world.getRandom()));
		return ((HarvestableStructure) getDefinition()).getItemReward().produce(random);
	}

	@Override
	public void writeData(ByteBuffer buffer, LongSequenceIOHelper idSequence) {
		super.writeData(buffer, idSequence);
		buffer.put(harvested ? (byte) 1 : (byte) 0);
	}

	@Override
	public void applyData(ByteBuffer buffer, LongSequenceIOHelper idSequence) {
		super.applyData(buffer, idSequence);
		harvested = buffer.get() == 1;
	}
}
