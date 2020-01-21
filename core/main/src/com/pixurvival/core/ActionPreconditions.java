package com.pixurvival.core;

import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.HarvestableMapStructure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.MapTile;
import com.pixurvival.core.map.chunk.ChunkPosition;

import lombok.experimental.UtilityClass;

/**
 * Contains all the methods that check if an action is feasible according to the
 * situation
 * 
 * 
 * @author SharkHendrix
 *
 */
@UtilityClass
public class ActionPreconditions {

	public static boolean canCraft(PlayerEntity playerEntity, ItemCraft itemCraft) {
		Structure requiredStructure = itemCraft.getRequiredStructure();
		if (requiredStructure != null) {
			MapStructure foundStructure = playerEntity.getWorld().getMap().findStructure(requiredStructure, (int) playerEntity.getPosition().getX(), (int) playerEntity.getPosition().getY());
			if (foundStructure == null || foundStructure.getDefinition() != requiredStructure
					|| playerEntity.getPosition().distanceSquared(foundStructure.getPosition()) > GameConstants.MAX_PLACE_STRUCTURE_DISTANCE * GameConstants.MAX_PLACE_STRUCTURE_DISTANCE) {
				return false;
			}
		}
		return playerEntity.getInventory().contains(itemCraft.getRecipes());
	}

	public static boolean canPlace(PlayerEntity player, Structure structure, int x, int y) {
		int x2 = x + structure.getDimensions().getWidth();
		int y2 = y + structure.getDimensions().getHeight();
		float centerX = (x + x2) / 2f;
		float centerY = (y + y2) / 2f;
		if (player.getPosition().distanceSquared(centerX, centerY) > GameConstants.MAX_PLACE_STRUCTURE_DISTANCE * GameConstants.MAX_PLACE_STRUCTURE_DISTANCE) {
			return false;
		}
		// TODO Remove when structure at multiple chunks solved
		if (!ChunkPosition.fromWorldPosition(x, y).equals(ChunkPosition.fromWorldPosition(x2 - 1, y2 - 1))) {
			return false;
		}
		for (int xi = x; xi < x2; xi++) {
			for (int yi = y; yi < y2; yi++) {
				MapTile mapTile = player.getWorld().getMap().tileAt(x, y);
				if (mapTile.isSolid() || mapTile.getStructure() != null || structure.getBannedTiles().contains(mapTile.getTileDefinition())) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean canInteract(PlayerEntity entity, MapStructure structure) {
		return entity.getCurrentAbility() == null && structure != null
				&& (structure instanceof HarvestableMapStructure && !((HarvestableMapStructure) structure).isHarvested() || structure.getDefinition().getDeconstructionDuration() > 0)
				&& entity.distanceSquared(structure.getPosition()) <= GameConstants.MAX_STRUCTURE_INTERACTION_DISTANCE * GameConstants.MAX_STRUCTURE_INTERACTION_DISTANCE;
	}
}
