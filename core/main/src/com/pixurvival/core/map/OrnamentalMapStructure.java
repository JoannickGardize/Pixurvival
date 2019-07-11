package com.pixurvival.core.map;

import java.nio.ByteBuffer;

import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.update.StructureUpdate;

public class OrnamentalMapStructure extends MapStructure {

	public OrnamentalMapStructure(Chunk chunk, Structure definition, int x, int y) {
		super(chunk, definition, x, y);
	}

	@Override
	public StructureUpdate getUpdate() {
		return null;
	}

	@Override
	public void writeData(ByteBuffer buffer) {
	}

	@Override
	public void applyData(ByteBuffer buffer) {
	}

}
