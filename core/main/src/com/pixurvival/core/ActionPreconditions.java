package com.pixurvival.core;

import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.interactionDialog.InteractionDialogHolder;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.HarvestableStructureEntity;
import com.pixurvival.core.map.MapTile;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.chunk.ChunkPosition;
import lombok.experimental.UtilityClass;

/**
 * Contains all the methods that check if an action is feasible according to the
 * situation
 *
 * @author SharkHendrix
 */
@UtilityClass
public class ActionPreconditions {

    public static boolean canCraft(PlayerEntity playerEntity, ItemCraft itemCraft) {
        Structure requiredStructure = itemCraft.getRequiredStructure();
        if (requiredStructure != null && playerEntity.getWorld().getMap().findClosestStructure(playerEntity.getPosition(), GameConstants.MAX_STRUCTURE_INTERACTION_DISTANCE,
                requiredStructure.getId()) == null) {
            return false;
        }
        return hasRequiredItems(playerEntity, itemCraft);
    }

    public static boolean hasRequiredItems(PlayerEntity playerEntity, ItemCraft itemCraft) {
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
        if (!ChunkPosition.fromWorldPosition(x, y).equals(ChunkPosition.fromWorldPosition(x2 - 1, y2 - 1))) {
            return false;
        }
        for (int xi = x; xi < x2; xi++) {
            for (int yi = y; yi < y2; yi++) {
                MapTile mapTile = player.getWorld().getMap().tileAt(x, y);
                if (mapTile.isSolid() || mapTile.getStructure() != null && !mapTile.getStructure().getDefinition().isAutoDestroy()
                        || structure.getBannedTiles().contains(mapTile.getTileDefinition())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean canInteract(LivingEntity entity, StructureEntity structure) {
        return structure != null
                && (structure instanceof HarvestableStructureEntity && !((HarvestableStructureEntity) structure).isHarvested()
                || structure.getDefinition().getDeconstructionDuration() > 0 || structure instanceof InteractionDialogHolder)
                && checkInteractionDistance(entity, structure);
    }

    public static boolean canDeconstruct(LivingEntity entity, StructureEntity structure) {
        return structure.getDefinition().getDeconstructionDuration() > 0 && checkInteractionDistance(entity, structure);
    }

    public static boolean checkInteractionDistance(Positionnable p1, Positionnable p2) {
        return p1.distanceSquared(p2) <= GameConstants.MAX_STRUCTURE_INTERACTION_DISTANCE * GameConstants.MAX_STRUCTURE_INTERACTION_DISTANCE;
    }
}
