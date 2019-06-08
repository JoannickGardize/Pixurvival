package com.pixurvival.gdxcore.input;

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

	/**
	 * I put this first because I AM left handed and I AM important.
	 * 
	 * @return
	 */
	public static InputMapping azertyLeftHanded() {
		InputMapping mapping = new InputMapping();
		mapping.bind(InputAction.MOVE_UP, InputButton.keyboard(Keys.O));
		mapping.bind(InputAction.MOVE_LEFT, InputButton.keyboard(Keys.K));
		mapping.bind(InputAction.MOVE_RIGHT, InputButton.keyboard(Keys.M));
		mapping.bind(InputAction.MOVE_DOWN, InputButton.keyboard(Keys.L));
		mapping.bind(InputAction.USE_ITEM_OR_STRUCTURE_INTERACTION, InputButton.mouse(Buttons.RIGHT));
		mapping.bind(InputAction.WEAPON_BASE_OR_DROP_ITEM, InputButton.mouse(Buttons.LEFT));
		mapping.bind(InputAction.WEAPON_SPECIAL, InputButton.keyboard(Keys.SPACE));
		mapping.bind(InputAction.ACCESSORY1_SPECIAL, InputButton.keyboard(Keys.I));
		mapping.bind(InputAction.ACCESSORY2_SPECIAL, InputButton.keyboard(Keys.P));
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
		return mapping;
	}

	public static InputMapping azertyRightHanded() {
		InputMapping mapping = new InputMapping();
		mapping.bind(InputAction.MOVE_UP, InputButton.keyboard(Keys.Z));
		mapping.bind(InputAction.MOVE_LEFT, InputButton.keyboard(Keys.Q));
		mapping.bind(InputAction.MOVE_RIGHT, InputButton.keyboard(Keys.D));
		mapping.bind(InputAction.MOVE_DOWN, InputButton.keyboard(Keys.S));
		mapping.bind(InputAction.USE_ITEM_OR_STRUCTURE_INTERACTION, InputButton.mouse(Buttons.RIGHT));
		mapping.bind(InputAction.WEAPON_BASE_OR_DROP_ITEM, InputButton.mouse(Buttons.LEFT));
		mapping.bind(InputAction.WEAPON_SPECIAL, InputButton.keyboard(Keys.SPACE));
		mapping.bind(InputAction.ACCESSORY1_SPECIAL, InputButton.keyboard(Keys.A));
		mapping.bind(InputAction.ACCESSORY2_SPECIAL, InputButton.keyboard(Keys.E));
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
		return mapping;
	}

}
