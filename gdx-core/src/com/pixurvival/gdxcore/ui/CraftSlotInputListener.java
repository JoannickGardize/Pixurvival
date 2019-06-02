package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.message.playerRequest.CraftItemRequest;
import com.pixurvival.gdxcore.PixurvivalGame;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CraftSlotInputListener extends InputListener {

	private ItemCraft itemCraft;

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		short quantity = 1;
		if (button == Input.Buttons.RIGHT) {
			quantity = 5;
		} else if (button != Input.Buttons.LEFT) {
			return false;
		}
		Inventory inventory = PixurvivalGame.getClient().getMyInventory();
		if (inventory.contains(itemCraft.getRecipes())) {
			PixurvivalGame.getClient().sendAction(new CraftItemRequest(itemCraft.getId(), quantity));
		}
		return true;
	}

	@Override
	public boolean mouseMoved(InputEvent event, float x, float y) {
		ItemCraftTooltip.getInstance().setVisible(true);
		ItemCraftTooltip.getInstance().setItemCraft(itemCraft);
		return true;
	}
}
