package com.pixurvival.core;

import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.MapTile;

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
			MapStructure foundStructure = playerEntity.getWorld().getMap().findClosestStructure((int) playerEntity.getPosition().getX(), (int) playerEntity.getPosition().getY());
			if (foundStructure == null
					|| playerEntity.getPosition().distanceSquared(foundStructure.getPosition()) > GameConstants.MAX_PLACE_STRUCTURE_DISTANCE * GameConstants.MAX_PLACE_STRUCTURE_DISTANCE) {
				return false;
			}
		}
		return playerEntity.getInventory().contains(itemCraft.getRecipes());
	}

	public static boolean canPlace(PlayerEntity player, Structure structure, int x, int y) {
		int x2 = x + structure.getDimensions().getWidth();
		int y2 = y + structure.getDimensions().getHeight();
		double centerX = (x + x2) / 2.0;
		double centerY = (y + y2) / 2.0;
		if (player.getPosition().distanceSquared(centerX, centerY) > GameConstants.MAX_PLACE_STRUCTURE_DISTANCE * GameConstants.MAX_PLACE_STRUCTURE_DISTANCE) {
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
}
