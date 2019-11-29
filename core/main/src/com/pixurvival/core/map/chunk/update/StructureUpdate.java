package com.pixurvival.core.map.chunk.update;

import com.pixurvival.core.map.chunk.Chunk;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class StructureUpdate {

	private int x;
	private int y;

	public abstract void apply(Chunk chunk);
}
