package com.pixurvival.gdxcore;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Input.Keys;

public class KeyMapping {

	private Map<KeyAction, Integer> keyByAction = new HashMap<>();
	private Map<Integer, KeyAction> actionByKey = new HashMap<>();

	public KeyMapping() {
		bind(KeyAction.MOVE_DOWN, Keys.DOWN);
		bind(KeyAction.MOVE_LEFT, Keys.LEFT);
		bind(KeyAction.MOVE_RIGHT, Keys.RIGHT);
		bind(KeyAction.MOVE_UP, Keys.UP);
	}

	public int getKey(KeyAction action) {
		return keyByAction.get(action);
	}

	public KeyAction getAction(int key) {
		return actionByKey.get(key);
	}

	public void bind(KeyAction action, int key) {
		keyByAction.put(action, key);
		actionByKey.put(key, action);
	}
}
