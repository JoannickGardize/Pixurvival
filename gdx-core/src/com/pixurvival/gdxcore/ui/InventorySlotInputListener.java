package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.message.playerRequest.DialogInteractionActionRequest;
import com.pixurvival.core.message.playerRequest.InventoryActionRequest;
import com.pixurvival.core.message.playerRequest.UseItemRequest;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.input.InputAction;
import com.pixurvival.gdxcore.input.InputManager;
import com.pixurvival.gdxcore.ui.tooltip.ItemTooltip;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InventorySlotInputListener extends InputListener {

	private Inventory inventory;
	private int slotIndex;

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			sendAction(inventory, slotIndex);
		} else if (button == Input.Buttons.RIGHT && isMyInventory(inventory)) {
			PixurvivalGame.getClient().sendAction(new UseItemRequest((short) slotIndex));
		}
		return true;
	}

	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			Actor actor = event.getStage().hit(event.getStageX(), event.getStageY(), true);
			if (actor instanceof InventorySlot && actor != event.getListenerActor()) {
				InventorySlot inventorySlot = (InventorySlot) actor;
				sendAction(inventorySlot.getInventory(), inventorySlot.getSlotIndex());
			}
		}
	}

	private void sendAction(Inventory inventory, int slotIndex) {
		if (isMyInventory(inventory)) {
			PixurvivalGame.getClient().sendAction(new InventoryActionRequest(slotIndex, isSplitMode()));
		} else {
			PixurvivalGame.getClient().sendAction(new DialogInteractionActionRequest(slotIndex, isSplitMode()));
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

	private boolean isSplitMode() {
		return InputManager.getInstance().getMapping().isActionPressed(InputAction.SPLIT_INVENTORY);
	}

	private boolean isMyInventory(Inventory inventory) {
		return inventory == PixurvivalGame.getClient().getMyInventory();
	}
}
