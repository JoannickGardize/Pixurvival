package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.Equipment;
import com.pixurvival.core.message.playerRequest.EquipmentActionRequest;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.tooltip.ItemTooltip;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EquipmentSlotInputListener extends InputListener {

	private int index;

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		if (button != Input.Buttons.LEFT) {
			return false;
		}
		ItemStack heldItemStack = PixurvivalGame.getClient().getMyInventory().getHeldItemStack();
		if (heldItemStack == null || Equipment.canEquip(index, heldItemStack)) {
			PixurvivalGame.getClient().sendAction(new EquipmentActionRequest((short) index));
		}
		return true;
	}

	@Override
	public boolean mouseMoved(InputEvent event, float x, float y) {
		ItemStack itemStack = PixurvivalGame.getClient().getMyPlayer().getEquipment().get(index);
		if (itemStack == null) {
			ItemTooltip.getInstance().setVisible(false);
		} else {
			ItemTooltip.getInstance().setItem(itemStack.getItem());
			ItemTooltip.getInstance().setVisible(true);
		}
		return true;
	}
}