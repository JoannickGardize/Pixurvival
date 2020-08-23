package com.pixurvival.gdxcore.input.processor;

import com.pixurvival.gdxcore.PixurvivalGame;

public class ChatProcessor implements InputActionProcessor {

	@Override
	public void buttonDown() {
	}

	@Override
	public void buttonUp() {
		PixurvivalGame.getInstance().focusChat();
	}

}
