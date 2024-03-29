package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.contentPack.item.AccessoryItem;
import com.pixurvival.core.contentPack.item.WeaponItem;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.Equipment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum EquipmentAbilityType {
    WEAPON_BASE(5, Equipment.WEAPON_INDEX, e -> e.getWeapon(), e -> e.getWeapon() == null ? null : ((WeaponItem) e.getWeapon().getItem()).getBaseAbility()),
    WEAPON_SPECIAL(6, Equipment.WEAPON_INDEX, e -> e.getWeapon(), e -> e.getWeapon() == null ? null : ((WeaponItem) e.getWeapon().getItem()).getSpecialAbility()),
    ACCESSORY1_SPECIAL(7, Equipment.ACCESSORY1_INDEX, e -> e.getAccessory1(), e -> e.getAccessory1() == null ? null : ((AccessoryItem) e.getAccessory1().getItem()).getAbility()),
    ACCESSORY2_SPECIAL(8, Equipment.ACCESSORY2_INDEX, e -> e.getAccessory2(), e -> e.getAccessory2() == null ? null : ((AccessoryItem) e.getAccessory2().getItem()).getAbility());

    private int abilityId;
    private int equipmentId;
    private Function<Equipment, ItemStack> itemGetter;
    private Function<Equipment, AlterationAbility> abilityGetter;
}
