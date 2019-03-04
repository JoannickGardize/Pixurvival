package com.pixurvival.gdxcore;

import java.nio.ByteBuffer;

import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.message.StructureUpdate;

import lombok.Getter;
import lombok.Setter;

public class GhostStructure extends MapStructure {

	@Getter
	@Setter
	private boolean valid;

	protected GhostStructure(Structure definition, int x, int y, boolean valid) {
		super(null, definition, x, y);
		this.valid = valid;
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
