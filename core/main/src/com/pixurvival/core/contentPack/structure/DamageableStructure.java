package com.pixurvival.core.contentPack.structure;

import com.pixurvival.core.map.DamageableMapStructure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.chunk.Chunk;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DamageableStructure extends Structure {

	private static final long serialVersionUID = 1L;

	private float maxHealth = 100;

	@Override
	public MapStructure newMapStructure(Chunk chunk, int x, int y) {
		return new DamageableMapStructure(chunk, this, x, y);
	}
}
