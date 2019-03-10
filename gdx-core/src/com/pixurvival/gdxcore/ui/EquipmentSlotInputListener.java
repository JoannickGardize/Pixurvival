package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.Equipment;
import com.pixurvival.core.message.request.EquipmentActionRequest;
import com.pixurvival.gdxcore.PixurvivalGame;

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

}