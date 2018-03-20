package com.pixurvival.core.map;

import java.util.Random;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.item.ItemStack;

import lombok.Getter;

@Getter
public class HarvestableStructure extends MapStructure {

	public HarvestableStructure(Structure definition, int x, int y) {
		super(definition, x, y);
	}

	private boolean harvested = false;

	public ItemStack[] harvest(Random random) {
		if (harvested) {
			Log.warn("warning, trying to harvest already harvested structure at " + getX() + ", " + getY());
			return new ItemStack[0];
		}
		harvested = true;
		return getDefinition().getItemReward().produce(random);
	}

}
