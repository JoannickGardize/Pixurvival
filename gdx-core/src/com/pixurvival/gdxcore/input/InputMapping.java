package com.pixurvival.gdxcore.input;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.gdxcore.util.Enums;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode(of = "buttonByAction")
public class InputMapping {

	private Map<InputAction, InputButton> buttonByAction = new EnumMap<>(InputAction.class);
	private Map<InputButton, InputAction> actionByButton = new HashMap<>();
	private String name;

	public InputMapping(Properties properties) {
		for (Entry<Object, Object> entry : properties.entrySet()) {
			InputAction action = Enums.valueOfOrNull(InputAction.class, (String) entry.getKey());
			if (action == null) {
				Log.warn("Unknown action: " + entry.getKey());
			} else {
				InputButton button = InputButton.fromCode((String) entry.getValue());
				if (button == null) {
					Log.warn("Unknown button code: " + entry.getValue());
				} else {
					bind(action, button);
				}
			}
		}
		// Bind missing actions with the default one
		InputMapping mappingDefaults = InputMappingDefaults.findBestDefaultMatch();
		for (Entry<InputButton, InputAction> defaultEntries : mappingDefaults.actionByButton.entrySet()) {
			if (getButton(defaultEntries.getValue()) == null) {
				bind(defaultEntries.getValue(), defaultEntries.getKey());
			}
		}
	}

	public InputButton getButton(InputAction action) {
		return buttonByAction.get(action);
	}

	public InputAction getAction(InputButton button) {
		return actionByButton.get(button);
	}

	public void bind(InputAction action, InputButton button) {
		actionByButton.remove(buttonByAction.get(action));
		buttonByAction.remove(actionByButton.get(button));
		buttonByAction.put(action, button);
		actionByButton.put(button, action);
	}

	public void set(InputMapping other) {
		buttonByAction.clear();
		buttonByAction.putAll(other.buttonByAction);
		actionByButton.clear();
		actionByButton.putAll(other.actionByButton);
	}

	public Properties toProperties() {
		Properties properties = new Properties();
		for (Entry<InputAction, InputButton> mapping : buttonByAction.entrySet()) {
			properties.put(mapping.getKey().toString(), mapping.getValue().toStringCode());
		}
		return properties;
	}

	@Override
	public String toString() {
		return name == null ? "Unnamed mapping" : name;
	}

	public InputMapping(String name) {
		super();
		this.name = name;
	}
}
