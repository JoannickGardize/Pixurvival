package com.pixurvival.core.contentPack.map;

import java.io.Serializable;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import lombok.Data;

@Data
public class StructureGenerator implements Serializable {

	private static final long serialVersionUID = 1L;

	private HeightmapCondition[] heightmapConditions;

	private StructureGeneratorEntry[] structureGeneratorEntries;
	private double probabilityFactor;

	private NavigableMap<Double, Structure> structureChooseMap;

	private double density;

	public boolean test(int x, int y) {
		for (HeightmapCondition h : heightmapConditions) {
			if (!h.test(x, y)) {
				return false;
			}
		}
		return true;
	}

	public Structure getStructure(int x, int y, Random random) {
		if (random.nextDouble() < density) {
			ensureChooseMapBuilt();
			return structureChooseMap.ceilingEntry(random.nextDouble() * probabilityFactor).getValue();
		} else {
			return null;
		}
	}

	private void ensureChooseMapBuilt() {
		if (structureChooseMap != null) {
			return;
		}
		structureChooseMap = new TreeMap<>();
		probabilityFactor = 0;
		for (StructureGeneratorEntry entry : structureGeneratorEntries) {
			probabilityFactor += entry.getProbability();
			structureChooseMap.put(probabilityFactor, entry.getStructure());
		}
	}
}
