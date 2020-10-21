package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.message.playerRequest.InventoryActionRequest;
import com.pixurvival.core.message.playerRequest.UseItemRequest;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ui.tooltip.ItemTooltip;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InventorySlotInputListener extends InputListener {

	private Inventory inventory;
	private int slotIndex;

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			PixurvivalGame.getClient().sendAction(new InventoryActionRequest(getInventoryActionType(), (short) slotIndex));
		} else if (button == Input.Buttons.RIGHT) {
			PixurvivalGame.getClient().sendAction(new UseItemRequest((short) slotIndex));
		}
		return true;
	}

	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			Actor actor = event.getStage().hit(event.getStageX(), event.getStageY(), true);
			if (actor instanceof InventorySlot && actor != event.getListenerActor()) {
				PixurvivalGame.getClient().sendAction(new InventoryActionRequest(getInventoryActionType(), (short) ((InventorySlot) actor).getSlotIndex()));
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

	private InventoryActionRequest.Type getInventoryActionType() {
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
			return InventoryActionRequest.Type.SPLIT_CLICK_MY_INVENTORY;
		} else {
			return InventoryActionRequest.Type.SWAP_CLICK_MY_INVENTORY;
		}
	}
}
