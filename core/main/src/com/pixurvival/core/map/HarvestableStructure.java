package com.pixurvival.core.map;

import com.pixurvival.core.item.ItemReward;

import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class HarvestableStructure extends MapStructure {

	private @Setter boolean harvested;

	public abstract ItemReward getItemReward();
}
