package com.pixurvival.core.message;

import com.pixurvival.core.map.Chunk;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class StructureUpdate {

	private int x;
	private int y;

	public abstract void perform(Chunk chunk);
}
