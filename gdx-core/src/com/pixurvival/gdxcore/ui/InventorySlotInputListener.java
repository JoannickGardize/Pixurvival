package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.pixurvival.core.message.InventoryActionRequest;
import com.pixurvival.core.message.InventoryActionRequest.Type;
import com.pixurvival.gdxcore.PixurvivalGame;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InventorySlotInputListener extends InputListener {

	private int slotIndex;

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		PixurvivalGame.getClient().sendAction(new InventoryActionRequest(Type.CURSOR_MY_INVENTORY, (short) slotIndex));
		return true;
	}

	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
		Actor actor = event.getStage().hit(event.getStageX(), event.getStageY(), true);
		if (actor instanceof InventorySlot && actor != event.getListenerActor()) {
			PixurvivalGame.getClient().sendAction(new InventoryActionRequest(Type.CURSOR_MY_INVENTORY,
					(short) ((InventorySlot) actor).getSlotIndex()));
		}
	}
}
