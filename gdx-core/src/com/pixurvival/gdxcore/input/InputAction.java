package com.pixurvival.gdxcore.input;

import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;
import com.pixurvival.gdxcore.input.processor.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum InputAction {
    MOVE_UP(new MoveProcessor()),
    MOVE_LEFT(new MoveProcessor()),
    MOVE_RIGHT(new MoveProcessor()),
    MOVE_DOWN(new MoveProcessor()),
    USE_ITEM_OR_STRUCTURE_INTERACTION(new UseItemOrStructureInteractionProcessor()),
    WEAPON_BASE_OR_DROP_ITEM(new WeaponBaseOrDropItemProcessor()),
    WEAPON_SPECIAL(new EquipmentAbilityProcessor(EquipmentAbilityType.WEAPON_SPECIAL)),
    ACCESSORY1_SPECIAL(new EquipmentAbilityProcessor(EquipmentAbilityType.ACCESSORY1_SPECIAL)),
    ACCESSORY2_SPECIAL(new EquipmentAbilityProcessor(EquipmentAbilityType.ACCESSORY2_SPECIAL)),
    INVENTORY1(new UseItemInInventoryProcessor(0)),
    INVENTORY2(new UseItemInInventoryProcessor(1)),
    INVENTORY3(new UseItemInInventoryProcessor(2)),
    INVENTORY4(new UseItemInInventoryProcessor(3)),
    INVENTORY5(new UseItemInInventoryProcessor(4)),
    INVENTORY6(new UseItemInInventoryProcessor(5)),
    INVENTORY7(new UseItemInInventoryProcessor(6)),
    INVENTORY8(new UseItemInInventoryProcessor(7)),
    INVENTORY9(new UseItemInInventoryProcessor(8)),
    INVENTORY10(new UseItemInInventoryProcessor(9)),
    SPLIT_INVENTORY(new InputActionProcessor() {
        // Does nothing on press / release
    }),
    CHAT(new ChatProcessor()),
    SWITCH_EQUIPMENT_AND_INVENTORY_UI(new SwitchEquimentAndInventoryUIProcessor()),
    SWITCH_CRAFT_UI(new SwitchCraftUIProcessor()),
    SWITCH_MINI_MAP_UI(new SwitchMiniMapUIProcessor()),
    SWITCH_CHAT_UI(new SwitchChatUIProcessor()),
    SWITCH_TIME_UI(new SwitchTimeUIProcessor()),
    SWITCH_ALL_UI(new SwitchAllUIProcessor()),
    SWITCH_DEBUG_MODE(new SwitchDebugModeProcessor()),
    SWITCH_DEBUG_INFOS(new SwitchDebugInfosProcessor()),
    SWITCH_FULLSCREEN(new SwitchFullScreenProcessor()),
    REQUEST_REFRESH(new RefreshProcessor()),
    PAUSE_MENU(new PauseMenuProcessor());

    private @Getter InputActionProcessor processor;

}
