package com.pixurvival.gdxcore.input;

import java.awt.im.InputContext;
import java.util.Locale;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

import lombok.experimental.UtilityClass;

/**
 * Factory methods for defaults input mappings.
 * 
 * @author SharkHendrix
 *
 */
@UtilityClass
public class InputMappingDefaults {

	public static InputMapping findBestDefaultMatch() {
		Locale locale = InputContext.getInstance().getLocale();
		if (locale.getLanguage().equals(Locale.FRENCH.getLanguage())) {
			return InputMappingDefaults.azerty();
		} else {
			return InputMappingDefaults.qwerty();
		}
	}

	public static InputMapping azerty() {
		InputMapping mapping = new InputMapping("AZERTY");
		commonPart(mapping);
		mapping.bind(InputAction.MOVE_UP, InputButton.keyboard(Keys.Z));
		mapping.bind(InputAction.MOVE_LEFT, InputButton.keyboard(Keys.Q));
		mapping.bind(InputAction.MOVE_RIGHT, InputButton.keyboard(Keys.D));
		mapping.bind(InputAction.MOVE_DOWN, InputButton.keyboard(Keys.S));
		mapping.bind(InputAction.ACCESSORY1_SPECIAL, InputButton.keyboard(Keys.A));
		mapping.bind(InputAction.ACCESSORY2_SPECIAL, InputButton.keyboard(Keys.E));
		return mapping;
	}

	public static InputMapping qwerty() {
		InputMapping mapping = new InputMapping("QWERTY");
		commonPart(mapping);
		mapping.bind(InputAction.MOVE_UP, InputButton.keyboard(Keys.W));
		mapping.bind(InputAction.MOVE_LEFT, InputButton.keyboard(Keys.A));
		mapping.bind(InputAction.MOVE_RIGHT, InputButton.keyboard(Keys.D));
		mapping.bind(InputAction.MOVE_DOWN, InputButton.keyboard(Keys.S));
		mapping.bind(InputAction.ACCESSORY1_SPECIAL, InputButton.keyboard(Keys.Q));
		mapping.bind(InputAction.ACCESSORY2_SPECIAL, InputButton.keyboard(Keys.E));
		return mapping;
	}

	private static void commonPart(InputMapping mapping) {
		mapping.bind(InputAction.WEAPON_SPECIAL, InputButton.keyboard(Keys.SPACE));
		mapping.bind(InputAction.USE_ITEM_OR_STRUCTURE_INTERACTION, InputButton.mouse(Buttons.RIGHT));
		mapping.bind(InputAction.WEAPON_BASE_OR_DROP_ITEM, InputButton.mouse(Buttons.LEFT));
		mapping.bind(InputAction.INVENTORY1, InputButton.keyboard(Keys.NUM_1));
		mapping.bind(InputAction.INVENTORY2, InputButton.keyboard(Keys.NUM_2));
		mapping.bind(InputAction.INVENTORY3, InputButton.keyboard(Keys.NUM_3));
		mapping.bind(InputAction.INVENTORY4, InputButton.keyboard(Keys.NUM_4));
		mapping.bind(InputAction.INVENTORY5, InputButton.keyboard(Keys.NUM_5));
		mapping.bind(InputAction.INVENTORY6, InputButton.keyboard(Keys.NUM_6));
		mapping.bind(InputAction.INVENTORY7, InputButton.keyboard(Keys.NUM_7));
		mapping.bind(InputAction.INVENTORY8, InputButton.keyboard(Keys.NUM_8));
		mapping.bind(InputAction.INVENTORY9, InputButton.keyboard(Keys.NUM_9));
		mapping.bind(InputAction.INVENTORY10, InputButton.keyboard(Keys.NUM_0));
		mapping.bind(InputAction.SWITCH_DEBUG_MODE, InputButton.keyboard(Keys.F1));
		mapping.bind(InputAction.SWITCH_DEBUG_INFOS, InputButton.keyboard(Keys.F2));
		mapping.bind(InputAction.SWITCH_FULLSCREEN, InputButton.keyboard(Keys.F11));
		mapping.bind(InputAction.REQUEST_REFRESH, InputButton.keyboard(Keys.F5));
		mapping.bind(InputAction.PAUSE_MENU, InputButton.keyboard(Keys.ESCAPE));
		mapping.bind(InputAction.CHAT, InputButton.keyboard(Keys.ENTER));
	}

}
