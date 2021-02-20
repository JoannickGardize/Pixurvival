package com.pixurvival.gdxcore.input;

import com.badlogic.gdx.InputAdapter;
import com.pixurvival.core.message.playerRequest.CloseInteractionDialogRequest;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.input.InputButton.Type;
import com.pixurvival.gdxcore.ui.tooltip.FactoryTooltip;
import com.pixurvival.gdxcore.ui.tooltip.ItemCraftTooltip;
import com.pixurvival.gdxcore.ui.tooltip.ItemTooltip;

public class WorldMouseProcessor extends InputAdapter {

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (PixurvivalGame.getClient().getMyPlayer().getInteractionDialog() != null) {
			PixurvivalGame.getClient().sendAction(new CloseInteractionDialogRequest());
		}
		InputManager.getInstance().buttonDown(new InputButton(Type.MOUSE, button));
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		InputManager.getInstance().buttonUp(new InputButton(Type.MOUSE, button));
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		processMouseMoved();
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		processMouseMoved();
		return true;
	}

	private void processMouseMoved() {
		ItemCraftTooltip.getInstance().setVisible(false);
		ItemTooltip.getInstance().setVisible(false);
		FactoryTooltip.getInstance().setVisible(false);
	}
}
