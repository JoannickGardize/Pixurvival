package com.pixurvival.gdxcore.ui;

import lombok.AllArgsConstructor;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.message.playerRequest.InventoryActionRequest;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.tooltip.ItemTooltip;

@AllArgsConstructor
public class InventorySlotInputListener extends InputListener {

	private Inventory inventory;
	private int slotIndex;

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		InventoryActionRequest.Type type = getButtonActionType(button);
		if (type == null) {
			return true;
		}
		PixurvivalGame.getClient().sendAction(new InventoryActionRequest(type, (short) slotIndex));
		return true;
	}

	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
		Actor actor = event.getStage().hit(event.getStageX(), event.getStageY(), true);
		if (actor instanceof InventorySlot && actor != event.getListenerActor()) {
			InventoryActionRequest.Type type = getButtonActionType(button);
			if (type != null) {
				PixurvivalGame.getClient().sendAction(new InventoryActionRequest(type, (short) ((InventorySlot) actor).getSlotIndex()));
			}
		}
	}

	@Override
	public boolean mouseMoved(InputEvent event, float x, float y) {
		ItemStack itemStack = inventory.getSlot(slotIndex);
		if (itemStack == null) {
			ItemTooltip.getInstance().setVisible(false);
		} else {
			ItemTooltip.getInstance().setItem(itemStack.getItem());
			ItemTooltip.getInstance().setVisible(true);
		}
		return true;
	}

	private InventoryActionRequest.Type getButtonActionType(int button) {
		if (button == Input.Buttons.LEFT) {
			return InventoryActionRequest.Type.SWAP_CLICK_MY_INVENTORY;
		} else if (button == Input.Buttons.RIGHT) {
			return InventoryActionRequest.Type.SPLIT_CLICK_MY_INVENTORY;
		} else {
			return null;
		}
	}
}
