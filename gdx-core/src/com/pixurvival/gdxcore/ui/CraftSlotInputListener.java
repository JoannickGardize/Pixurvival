package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.pixurvival.core.ActionPreconditions;
import com.pixurvival.core.message.playerRequest.CraftItemRequest;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.tooltip.ItemCraftTooltip;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CraftSlotInputListener extends InputListener {

	private CraftSlot craftSlot;

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		short quantity = 1;
		if (button == Input.Buttons.RIGHT) {
			quantity = 5;
		} else if (button != Input.Buttons.LEFT) {
			return false;
		}
		if (ActionPreconditions.canCraft(PixurvivalGame.getClient().getMyPlayer(), craftSlot.getItemCraft())) {
			PixurvivalGame.getClient().sendAction(new CraftItemRequest(craftSlot.getItemCraft().getId(), quantity));
		}
		return true;
	}

	@Override
	public boolean mouseMoved(InputEvent event, float x, float y) {
		ItemCraftTooltip.getInstance().setVisible(true);
		ItemCraftTooltip.getInstance().setItemCraft(craftSlot.getItemCraft());
		if (craftSlot.isNewlyDiscovered()) {
			craftSlot.setNewlyDiscovered(false);
			craftSlot.invalidate();
		}
		return true;
	}
}
