package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.pixurvival.core.message.playerRequest.InventoryActionRequest;
import com.pixurvival.gdxcore.PixurvivalGame;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InventorySlotInputListener extends InputListener {

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
