package com.pixurvival.gdxcore.input;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class InputMapping {

	private Map<InputAction, InputButton> buttonByAction = new EnumMap<>(InputAction.class);
	private Map<InputButton, InputAction> actionByButton = new HashMap<>();

	public InputMapping() {
	}

	public InputButton getButton(InputAction action) {
		return buttonByAction.get(action);
	}

	public InputAction getAction(InputButton button) {
		return actionByButton.get(button);
	}

	public void bind(InputAction action, InputButton button) {
		buttonByAction.put(action, button);
		actionByButton.put(button, action);
	}
}
