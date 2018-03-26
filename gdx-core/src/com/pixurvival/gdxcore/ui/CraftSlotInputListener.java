package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.pixurvival.core.aliveEntity.Activity;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.message.CraftItemRequest;
import com.pixurvival.gdxcore.PixurvivalGame;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CraftSlotInputListener extends InputListener {

	private ItemCraft itemCraft;

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		Inventory inventory = PixurvivalGame.getClient().getMyInventory();
		PlayerEntity player = PixurvivalGame.getClient().getMyPlayer();
		if (inventory.fastContainsAll(itemCraft.getRecipes())
				&& player.getActivity().in(Activity.NONE_ID, Activity.CRAFTING_ACTIVITY_ID)) {
			PixurvivalGame.getClient().sendAction(new CraftItemRequest(itemCraft.getId(), (short) 1));
		}
		return true;
	}
}
