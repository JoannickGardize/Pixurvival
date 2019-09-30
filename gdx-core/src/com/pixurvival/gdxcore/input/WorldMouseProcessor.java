package com.pixurvival.gdxcore.input;

import com.badlogic.gdx.InputAdapter;
import com.pixurvival.gdxcore.input.InputButton.Type;
import com.pixurvival.gdxcore.ui.tooltip.ItemCraftTooltip;
import com.pixurvival.gdxcore.ui.tooltip.ItemTooltip;

public class WorldMouseProcessor extends InputAdapter {

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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
	}
}
