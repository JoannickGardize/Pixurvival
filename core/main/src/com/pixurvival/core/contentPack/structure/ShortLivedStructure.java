package com.pixurvival.core.contentPack.structure;

import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.ShortLivedMapStructure;
import com.pixurvival.core.map.chunk.Chunk;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortLivedStructure extends Structure {

	private static final long serialVersionUID = 1L;

	@Bounds(min = 0)
	private long duration;

	@Override
	public MapStructure newMapStructure(Chunk chunk, int x, int y) {
		return new ShortLivedMapStructure(chunk, this, x, y);
	}
}
