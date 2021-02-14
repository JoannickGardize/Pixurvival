package com.pixurvival.core.map;

import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.map.chunk.Chunk;

public class FactoryMapStructure extends MapStructure {

	public FactoryMapStructure(Chunk chunk, Structure definition, int x, int y) {
		super(chunk, definition, x, y);
	}

}
