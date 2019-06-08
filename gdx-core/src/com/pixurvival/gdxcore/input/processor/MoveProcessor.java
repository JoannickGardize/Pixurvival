package com.pixurvival.gdxcore.input.processor;

import com.pixurvival.gdxcore.input.InputManager;

public class MoveProcessor implements InputActionProcessor {

	@Override
	public void buttonDown() {
		InputManager m = InputManager.getInstance();
		m.setPlayerMovementChanged(true);
	}

	@Override
	public void buttonUp() {
		InputManager.getInstance().setPlayerMovementChanged(true);
	}

}
