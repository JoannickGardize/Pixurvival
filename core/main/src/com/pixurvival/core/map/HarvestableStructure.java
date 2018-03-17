package com.pixurvival.core.map;

import com.pixurvival.core.contentPack.map.Structure;

import lombok.Getter;
import lombok.Setter;

@Getter
public class HarvestableStructure extends MapStructure {

	public HarvestableStructure(Structure definition, int x, int y) {
		super(definition, x, y);
	}

	private @Setter boolean harvested;

}
