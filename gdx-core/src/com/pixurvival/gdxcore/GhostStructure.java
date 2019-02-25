package com.pixurvival.gdxcore;

import java.nio.ByteBuffer;

import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.message.StructureUpdate;

public class GhostStructure extends MapStructure {

	protected GhostStructure(Structure definition, int x, int y) {
		super(null, definition, x, y);
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
