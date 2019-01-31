package com.pixurvival.core.contentPack.map;

import java.io.Serializable;

import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;

import lombok.Data;

@Data
public class HeightmapCondition implements Serializable {

	private static final long serialVersionUID = 1L;

	@Required
	@ElementReference(depth = 4)
	private Heightmap heightmap;

	@Bounds(min = 0, max = 1, maxInclusive = true)
	private double min;

	@Bounds(min = 0, max = 1, maxInclusive = true)
	private double max;

	public boolean test(int x, int y) {
		double noise = heightmap.getNoise(x, y);
		return noise >= min && noise < max;
	}
}
