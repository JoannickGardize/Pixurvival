package com.pixurvival.gdxcore;

import com.pixurvival.core.Collidable;
import com.pixurvival.core.contentPack.map.Structure;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GhostStructure implements Collidable {

	private Structure structure;
	private double x;
	private double y;

	@Override
	public double getHalfWidth() {
		return 0.5;
	}

	@Override
	public double getHalfHeight() {
		return 0.5;
	}

}
