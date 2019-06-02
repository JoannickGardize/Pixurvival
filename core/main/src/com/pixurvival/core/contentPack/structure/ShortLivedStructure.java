package com.pixurvival.core.contentPack.structure;

import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.ShortLivedMapStructure;
import com.pixurvival.core.map.MapStructure;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortLivedStructure extends Structure {

	private static final long serialVersionUID = 1L;

	@Bounds(min = 0)
	private double duration;

	@Override
	public MapStructure newMapStructure(Chunk chunk, int x, int y) {
		return new ShortLivedMapStructure(chunk, this, x, y);
	}
}
