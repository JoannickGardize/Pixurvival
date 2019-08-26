package com.pixurvival.gdxcore.input.processor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.contentPack.item.StructureItem;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.PlayerInventory;
import com.pixurvival.core.map.HarvestableMapStructure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.message.playerRequest.InteractStructureRequest;
import com.pixurvival.core.message.playerRequest.PlaceStructureRequest;
import com.pixurvival.core.message.playerRequest.UseItemRequest;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.WorldScreen;

public class UseItemOrStructureInteractionProcessor implements InputActionProcessor {

	public static final int FIND_STRUCTURE_RADIUS = 1;

	@Override
	public void buttonDown() {
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		ItemStack heldItemStack = myPlayer.getInventory().getHeldItemStack();
		if (heldItemStack != null && heldItemStack.getItem() instanceof StructureItem) {
			Vector2 worldPoint = getWorldCursorPosition();
			int x = MathUtils.floor(worldPoint.x);
			int y = MathUtils.floor(worldPoint.y);
			if (MapStructure.canPlace(myPlayer, PixurvivalGame.getWorld().getMap(), ((StructureItem) heldItemStack.getItem()).getStructure(), x, y)) {
				PixurvivalGame.getClient().sendAction(new PlaceStructureRequest(x, y));
			}
		} else if (heldItemStack != null && heldItemStack.getItem() instanceof EdibleItem) {
			PixurvivalGame.getClient().sendAction(new UseItemRequest(PlayerInventory.HELD_ITEM_STACK_INDEX));
		} else {
			MapStructure structure = findClosestStructure();
			if (structure instanceof HarvestableMapStructure && structure.canInteract(myPlayer)) {
				PixurvivalGame.getClient().sendAction(new InteractStructureRequest(structure.getTileX(), structure.getTileY()));
			}
		}
	}

	private MapStructure findClosestStructure() {
		Vector2 worldPoint = getWorldCursorPosition();
		int x = MathUtils.floor(worldPoint.x);
		int y = MathUtils.floor(worldPoint.y);
		TiledMap map = PixurvivalGame.getWorld().getMap();
		MapStructure closest = null;
		double closestDist = Double.POSITIVE_INFINITY;
		for (int dx = x - FIND_STRUCTURE_RADIUS; dx <= x + FIND_STRUCTURE_RADIUS; dx++) {
			for (int dy = y - FIND_STRUCTURE_RADIUS; dy <= y + FIND_STRUCTURE_RADIUS; dy++) {
				MapStructure structure = map.tileAt(dx, dy).getStructure();
				if (structure != null) {
					double diffX = structure.getPosition().getX() - worldPoint.x;
					double diffY = structure.getPosition().getY() - worldPoint.y;
					double dist = diffX * diffX + diffY * diffY;
					if (dist < closestDist) {
						closestDist = dist;
						closest = structure;
					}
				}
			}
		}
		return closest;
	}

	private Vector2 getWorldCursorPosition() {
		return WorldScreen.getWorldStage().getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
	}

	@Override
	public void buttonUp() {
		// Nothing
	}

}
