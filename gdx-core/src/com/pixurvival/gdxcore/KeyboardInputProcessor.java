package com.pixurvival.gdxcore;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.InputAdapter;
import com.pixurvival.core.message.Direction;
import com.pixurvival.core.message.PlayerActionRequest;

import lombok.Getter;

public class KeyboardInputProcessor extends InputAdapter {

	private KeyMapping keyMapping;
	private @Getter PlayerActionRequest playerAction = new PlayerActionRequest();
	private PlayerActionRequest previousPlayerAction = new PlayerActionRequest();
	private Map<KeyAction, Boolean> pressedKeys = new HashMap<>();

	public KeyboardInputProcessor(KeyMapping keyMapping) {
		this.keyMapping = keyMapping;
		playerAction.setDirection(Direction.SOUTH);
		playerAction.setForward(false);
		for (KeyAction action : KeyAction.values()) {
			pressedKeys.put(action, false);
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		KeyAction keyAction = keyMapping.getAction(keycode);
		if (keyAction != null) {
			pressedKeys.put(keyAction, true);
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		KeyAction keyAction = keyMapping.getAction(keycode);
		if (keyAction != null) {
			pressedKeys.put(keyAction, false);
		}
		return true;
	}

	/**
	 * @return true if playerActionRequest has changed
	 */
	public boolean update() {
		boolean left = pressedKeys.get(KeyAction.MOVE_LEFT);
		boolean up = pressedKeys.get(KeyAction.MOVE_UP);
		boolean right = pressedKeys.get(KeyAction.MOVE_RIGHT);
		boolean down = pressedKeys.get(KeyAction.MOVE_DOWN);

		playerAction.setForward(true);
		if (right && up) {
			playerAction.setDirection(Direction.NORTH_EAST);
		} else if (up && left) {
			playerAction.setDirection(Direction.NORTH_WEST);
		} else if (left && down) {
			playerAction.setDirection(Direction.SOUTH_WEST);
		} else if (down && right) {
			playerAction.setDirection(Direction.SOUTH_EAST);
		} else if (right) {
			playerAction.setDirection(Direction.EAST);
		} else if (up) {
			playerAction.setDirection(Direction.NORTH);
		} else if (left) {
			playerAction.setDirection(Direction.WEST);
		} else if (down) {
			playerAction.setDirection(Direction.SOUTH);
		} else {
			playerAction.setForward(false);
		}

		if (playerAction.equals(previousPlayerAction)) {
			return false;
		} else {
			previousPlayerAction.set(playerAction);
			return true;
		}
	}
}
