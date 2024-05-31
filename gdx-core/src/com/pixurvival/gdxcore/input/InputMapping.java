package com.pixurvival.gdxcore.input;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.gdxcore.util.Enums;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.Map.Entry;

@NoArgsConstructor
@EqualsAndHashCode(of = "buttonByAction")
public class InputMapping {

    public static final int VERSION = 2;
    public static final String VERSION_KEY = "version";

    private Map<InputAction, InputButton> buttonByAction = new EnumMap<>(InputAction.class);
    private Map<InputButton, Set<InputAction>> actionByButton = new HashMap<>();
    private String name;

    public InputMapping(Properties properties) {
        // Bind default first so new binding will not conflict with actual user binding
        InputMapping mappingDefaults = InputMappingDefaults.findBestDefaultMatch();
        for (Entry<InputButton, Set<InputAction>> defaultEntries : mappingDefaults.actionByButton.entrySet()) {
            bind(defaultEntries.getValue().iterator().next(), defaultEntries.getKey());
        }

        if (!String.valueOf(VERSION).equals(properties.get("version"))) {
            return;
        }
        properties.remove("version");
        // Binding user values
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
    }

    public InputButton getButton(InputAction action) {
        return buttonByAction.get(action);
    }

    public Set<InputAction> getAction(InputButton button) {
        return actionByButton.get(button);
    }

    public void bind(InputAction action, InputButton button) {
        InputButton previousButton = buttonByAction.remove(action);
        Set<InputAction> actions = actionByButton.get(previousButton);
        if (actions != null) {
            actions.remove(action);
        }
        buttonByAction.put(action, button);
        actionByButton.merge(button, EnumSet.of(action), (e1, e2) -> {
            e1.addAll(e2);
            return e1;
        });
    }

    public void set(InputMapping other) {
        buttonByAction.clear();
        buttonByAction.putAll(other.buttonByAction);
        actionByButton.clear();
        actionByButton.putAll(other.actionByButton);
    }

    public Properties toProperties() {
        Properties properties = new Properties();
        properties.put(VERSION_KEY, String.valueOf(VERSION));
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

    public boolean isActionPressed(InputAction action) {
        return buttonByAction.get(action).isPressed();
    }
}
