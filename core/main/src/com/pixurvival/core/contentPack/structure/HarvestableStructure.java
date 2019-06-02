package com.pixurvival.core.contentPack.structure;

import com.pixurvival.core.contentPack.DoubleInterval;
import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.HarvestableMapStructure;
import com.pixurvival.core.map.MapStructure;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HarvestableStructure extends Structure {

	private static final long serialVersionUID = 1L;

	@Bounds(min = 0)
	private double harvestingTime;

	@Required
	@ElementReference
	private ItemReward itemReward;

	@Valid
	@Required
	private DoubleInterval respawnTime = new DoubleInterval();

	@Override
	public MapStructure newMapStructure(Chunk chunk, int x, int y) {
		return new HarvestableMapStructure(chunk, this, x, y);
	}
}
