package com.pixurvival.gdxcore.input;

import com.badlogic.gdx.InputAdapter;
import com.pixurvival.gdxcore.input.InputButton.Type;

public class WorldKeyboardProcessor extends InputAdapter {

	@Override
	public boolean keyDown(int keycode) {
		InputManager.getInstance().buttonDown(new InputButton(Type.KEYBOARD, keycode));
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		InputManager.getInstance().buttonUp(new InputButton(Type.KEYBOARD, keycode));
		return true;
	}

}
