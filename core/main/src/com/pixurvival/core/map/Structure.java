package com.pixurvival.core.map;

import lombok.Getter;

@Getter
public abstract class Structure {

	private int x;
	private int y;

	public abstract boolean isSolid();

	public abstract int getWeight();

	public abstract int getHeight();
}
