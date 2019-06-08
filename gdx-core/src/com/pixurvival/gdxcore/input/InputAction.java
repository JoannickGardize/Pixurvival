package com.pixurvival.gdxcore.input;

import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;
import com.pixurvival.gdxcore.input.processor.EquipmentAbilityProcessor;
import com.pixurvival.gdxcore.input.processor.InputActionProcessor;
import com.pixurvival.gdxcore.input.processor.MoveProcessor;
import com.pixurvival.gdxcore.input.processor.StructureInteractionProcessor;
import com.pixurvival.gdxcore.input.processor.SwitchDebugInfosProcessor;
import com.pixurvival.gdxcore.input.processor.SwitchDebugModeProcessor;
import com.pixurvival.gdxcore.input.processor.WeaponBaseOrDropItemProcessor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum InputAction {
	MOVE_UP(new MoveProcessor()),
	MOVE_LEFT(new MoveProcessor()),
	MOVE_RIGHT(new MoveProcessor()),
	MOVE_DOWN(new MoveProcessor()),
	USE_ITEM_OR_STRUCTURE_INTERACTION(new StructureInteractionProcessor()),
	WEAPON_BASE_OR_DROP_ITEM(new WeaponBaseOrDropItemProcessor()),
	WEAPON_SPECIAL(new EquipmentAbilityProcessor(EquipmentAbilityType.WEAPON_SPECIAL)),
	ACCESSORY1_SPECIAL(new EquipmentAbilityProcessor(EquipmentAbilityType.WEAPON_SPECIAL)),
	ACCESSORY2_SPECIAL(new EquipmentAbilityProcessor(EquipmentAbilityType.WEAPON_SPECIAL)),
	INVENTORY1(null), // TODO Processors EdibleItem
	INVENTORY2(null),
	INVENTORY3(null),
	INVENTORY4(null),
	INVENTORY5(null),
	INVENTORY6(null),
	INVENTORY7(null),
	INVENTORY8(null),
	INVENTORY9(null),
	INVENTORY10(null),
	SWITCH_DEBUG_MODE(new SwitchDebugModeProcessor()),
	SWITCH_DEBUG_INFOS(new SwitchDebugInfosProcessor());

	private @Getter InputActionProcessor processor;

}
