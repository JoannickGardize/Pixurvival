package com.pixurvival.core.map;

import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;

import com.pixurvival.core.Collidable;
import com.pixurvival.core.Entity;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.StructureType;
import com.pixurvival.core.message.StructureUpdate;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;

@Getter
public abstract class MapStructure implements Collidable {

	@FunctionalInterface
	private static interface StructureSupplier {
		MapStructure apply(Chunk chunk, Structure structure, int x, int y);
	}

	private static Map<StructureType, StructureSupplier> mapStructureCreator = new EnumMap<>(StructureType.class);

	static {
		mapStructureCreator.put(StructureType.HARVESTABLE, (c, s, x, y) -> new HarvestableStructure(c, s, x, y));
		mapStructureCreator.put(StructureType.SHORT_LIVED, (c, s, x, y) -> new ShortLivedStructure(c, s, x, y));
	}

	private Chunk chunk;
	private Structure definition;
	private int tileX;
	private int tileY;
	private Vector2 position;

	protected MapStructure(Chunk chunk, Structure definition, int x, int y) {
		this.chunk = chunk;
		this.definition = definition;
		tileX = x;
		tileY = y;
		position = new Vector2(x + definition.getDimensions().getWidth() / 2.0,
				y + definition.getDimensions().getHeight() / 2.0);
	}

	public static MapStructure newInstance(Chunk chunk, Structure structure, int x, int y) {
		return mapStructureCreator.get(structure.getType()).apply(chunk, structure, x, y);
	}

	@Override
	public double getHalfWidth() {
		return definition.getDimensions().getWidth() / 2.0;
	}

	@Override
	public double getHalfHeight() {
		return definition.getDimensions().getHeight() / 2.0;
	}

	public int getWidth() {
		return definition.getDimensions().getWidth();
	}

	public int getHeight() {
		return definition.getDimensions().getHeight();
	}

	@Override
	public double getX() {
		return position.x;
	}

	@Override
	public double getY() {
		return position.y;
	}

	public abstract StructureUpdate getUpdate();

	public boolean canInteract(Entity entity) {
		return entity.distanceSquared(getPosition()) <= GameConstants.MAX_HARVEST_DISTANCE
				* GameConstants.MAX_HARVEST_DISTANCE;
	}

	public abstract void writeData(ByteBuffer buffer);

	public abstract void applyData(ByteBuffer buffer);
}
