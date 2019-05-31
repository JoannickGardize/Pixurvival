package com.pixurvival.core.livingEntity.ability;

import java.util.function.Function;

import com.pixurvival.core.item.AccessoryItem;
import com.pixurvival.core.item.WeaponItem;
import com.pixurvival.core.livingEntity.Equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EquipmentAbilityType {
	WEAPON_BASE(e -> e.getWeapon() == null ? null : ((WeaponItem) e.getWeapon().getItem()).getBaseAbility()),
	WEAPON_SPECIAL(e -> e.getWeapon() == null ? null : ((WeaponItem) e.getWeapon().getItem()).getSpecialAbility()),
	ACCESSORY1_SPECIAL(e -> e.getAccessory1() == null ? null : ((AccessoryItem) e.getAccessory1().getItem()).getAbility()),
	ACCESSORY2_SPECIAL(e -> e.getAccessory2() == null ? null : ((AccessoryItem) e.getAccessory2().getItem()).getAbility());

	private Function<Equipment, Ability> abilityGetter;
}
