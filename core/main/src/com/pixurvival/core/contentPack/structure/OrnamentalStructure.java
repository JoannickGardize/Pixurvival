package com.pixurvival.core.contentPack.structure;

import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.OrnamentalMapStructure;

public class OrnamentalStructure extends Structure {

	private static final long serialVersionUID = 1L;

	@Override
	public MapStructure newMapStructure(Chunk chunk, int x, int y) {
		return new OrnamentalMapStructure(chunk, this, x, y);
	}

}
