package com.pixurvival.core.map;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.pixurvival.core.Collidable;
import com.pixurvival.core.CustomDataHolder;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.aliveEntity.Activity;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.Structure.Details;
import com.pixurvival.core.message.StructureUpdate;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class MapStructure implements Collidable, CustomDataHolder {

	@FunctionalInterface
	private static interface StructureSupplier {
		MapStructure apply(Chunk chunk, Structure structure, int x, int y);
	}

	private static Map<Class<? extends Details>, StructureSupplier> mapStructureCreator = new HashMap<>();

	static {
		mapStructureCreator.put(Structure.Harvestable.class, HarvestableStructure::new);
		mapStructureCreator.put(Structure.ShortLived.class, ShortLivedStructure::new);
	}

	private Chunk chunk;
	private Structure definition;
	private int tileX;
	private int tileY;
	private Vector2 position;
	private @Setter Object customData;

	protected MapStructure(Chunk chunk, Structure definition, int x, int y) {
		this.chunk = chunk;
		this.definition = definition;
		tileX = x;
		tileY = y;
		position = new Vector2(x + definition.getDimensions().getWidth() / 2.0,
				y + definition.getDimensions().getHeight() / 2.0);
	}

	public static MapStructure newInstance(Chunk chunk, Structure structure, int x, int y) {
		return mapStructureCreator.get(structure.getDetails().getClass()).apply(chunk, structure, x, y);
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

	public boolean canInteract(PlayerEntity entity) {
		return entity.getActivity() == Activity.NONE
				&& entity.distanceSquared(getPosition()) <= GameConstants.MAX_HARVEST_DISTANCE
						* GameConstants.MAX_HARVEST_DISTANCE;
	}

	public static boolean canPlace(PlayerEntity player, TiledMap map, Structure structure, int x, int y) {
		int x2 = x + structure.getDimensions().getWidth();
		int y2 = y + structure.getDimensions().getHeight();
		double centerX = (x + x2) / 2.0;
		double centerY = (y + y2) / 2.0;
		if (player.getPosition().distanceSquared(centerX, centerY) > GameConstants.MAX_PLACE_STRUCTURE_DISTANCE
				* GameConstants.MAX_PLACE_STRUCTURE_DISTANCE) {
			return false;
		}
		for (int xi = x; xi < x2; xi++) {
			for (int yi = y; yi < y2; yi++) {
				MapTile mapTile = map.tileAt(x, y);
				if (mapTile.isSolid() || mapTile.getStructure() != null) {
					return false;
				}
			}
		}
		return true;
	}

	public abstract void writeData(ByteBuffer buffer);

	public abstract void applyData(ByteBuffer buffer);
}
