package com.pixurvival.core.contentPack.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;

import com.pixurvival.core.contentPack.WeightedValueProducer;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.Data;

@Data
public class StructureGenerator implements Serializable {

	private static final long serialVersionUID = 1L;

	private WeightedValueProducer<Structure> structureProducer = new WeightedValueProducer<>();

	@Valid
	private List<HeightmapCondition> heightmapConditions = new ArrayList<>();

	private transient float probabilityWeight;

	private transient NavigableMap<Float, Structure> structureChooseMap;

	@Bounds(min = 0, max = 1, maxInclusive = true)
	private float density;

	public boolean test(int x, int y) {
		for (HeightmapCondition h : heightmapConditions) {
			if (!h.test(x, y)) {
				return false;
			}
		}
		return true;
	}

	public Structure next(Tile tile, Random random) {
		if (random.nextFloat() >= density) {
			return null;
		} else {
			Structure structure = structureProducer.next(random);
			if (structure.getBannedTiles().contains(tile)) {
				return null;
			} else {
				return structure;
			}
		}
	}
}
