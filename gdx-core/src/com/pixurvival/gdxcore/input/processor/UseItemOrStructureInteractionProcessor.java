package com.pixurvival.gdxcore.input.processor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.pixurvival.core.ActionPreconditions;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.contentPack.item.StructureItem;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.PlayerInventory;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.message.playerRequest.InteractStructureRequest;
import com.pixurvival.core.message.playerRequest.PlaceStructureRequest;
import com.pixurvival.core.message.playerRequest.UseItemRequest;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.WorldScreen;

public class UseItemOrStructureInteractionProcessor implements InputActionProcessor {

	@Override
	public void buttonDown() {
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		ItemStack heldItemStack = myPlayer.getInventory().getHeldItemStack();
		if (heldItemStack != null && heldItemStack.getItem() instanceof StructureItem) {
			Vector2 worldPoint = getWorldCursorPosition();
			int x = MathUtils.floor(worldPoint.x);
			int y = MathUtils.floor(worldPoint.y);
			if (ActionPreconditions.canPlace(myPlayer, ((StructureItem) heldItemStack.getItem()).getStructure(), x, y)) {
				PixurvivalGame.getClient().sendAction(new PlaceStructureRequest(x, y));
			}
		} else if (heldItemStack != null && heldItemStack.getItem() instanceof EdibleItem) {
			PixurvivalGame.getClient().sendAction(new UseItemRequest(PlayerInventory.HELD_ITEM_STACK_INDEX));
		} else {
			Vector2 position = getWorldCursorPosition();
			MapStructure structure = myPlayer.getWorld().getMap().findClosestStructure(new com.pixurvival.core.util.Vector2(position.x, position.y), GameConstants.MAX_STRUCTURE_INTERACTION_DISTANCE);
			if (ActionPreconditions.canInteract(myPlayer, structure)) {
				PixurvivalGame.getClient().sendAction(new InteractStructureRequest(structure.getTileX(), structure.getTileY()));
			}
		}
	}

	private Vector2 getWorldCursorPosition() {
		return WorldScreen.getWorldStage().getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
	}
}
