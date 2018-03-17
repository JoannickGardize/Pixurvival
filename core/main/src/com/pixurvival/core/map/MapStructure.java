package com.pixurvival.core.map;

import java.util.EnumMap;
import java.util.Map;

import com.pixurvival.core.Collidable;
import com.pixurvival.core.contentPack.StructureType;
import com.pixurvival.core.contentPack.map.Structure;

import lombok.Getter;

@Getter
public abstract class MapStructure implements Collidable {

	@FunctionalInterface
	private static interface StructureSupplier {
		MapStructure apply(Structure structure, int x, int y);
	}

	private static Map<StructureType, StructureSupplier> mapStructureCreator = new EnumMap<>(StructureType.class);

	static {
		mapStructureCreator.put(StructureType.HARVESTABLE, (s, x, y) -> new HarvestableStructure(s, x, y));
	}

	private Structure definition;
	private int tileX;
	private int tileY;
	private double x;
	private double y;

	public MapStructure(Structure definition, int x, int y) {
		this.definition = definition;
		tileX = x;
		tileY = y;
		this.x = x + definition.getDimensions().getWidth() / 2.0;
		this.y = y + definition.getDimensions().getHeight() / 2.0;
	}

	public static MapStructure fromStructure(Structure structure, int x, int y) {
		return mapStructureCreator.get(structure.getType()).apply(structure, x, y);
	}

	@Override
	public double getHalfWidth() {
		return definition.getDimensions().getWidth() / 2.0;
	}

	@Override
	public double getHalfHeight() {
		return definition.getDimensions().getHeight() / 2.0;
	}
}
