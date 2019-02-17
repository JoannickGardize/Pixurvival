package com.pixurvival.core.contentPack.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.Data;

@Data
public class StructureGenerator implements Serializable {

	private static final long serialVersionUID = 1L;

	@Valid
	private List<HeightmapCondition> heightmapConditions = new ArrayList<>();

	@Valid
	private List<StructureGeneratorEntry> structureGeneratorEntries = new ArrayList<>();

	private transient double probabilityWeight;

	private transient NavigableMap<Double, Structure> structureChooseMap;

	@Bounds(min = 0, max = 1, maxInclusive = true)
	private double density;

	public boolean test(int x, int y) {
		for (HeightmapCondition h : heightmapConditions) {
			if (!h.test(x, y)) {
				return false;
			}
		}
		return true;
	}

	public Structure next(Random random) {
		if (random.nextDouble() < density) {
			ensureChooseMapBuilt();
			return structureChooseMap.ceilingEntry(random.nextDouble() * probabilityWeight).getValue();
		} else {
			return null;
		}
	}

	private void ensureChooseMapBuilt() {
		if (structureChooseMap != null) {
			return;
		}
		structureChooseMap = new TreeMap<>();
		probabilityWeight = 0;
		for (StructureGeneratorEntry entry : structureGeneratorEntries) {
			probabilityWeight += entry.getProbability();
			structureChooseMap.put(probabilityWeight, entry.getStructure());
		}
	}
}
