package com.pixurvival.core.livingEntity.ability;

import java.util.function.Function;

import com.pixurvival.core.contentPack.item.AccessoryItem;
import com.pixurvival.core.contentPack.item.WeaponItem;
import com.pixurvival.core.livingEntity.Equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EquipmentAbilityType {
	WEAPON_BASE(2, e -> e.getWeapon() == null ? null : ((WeaponItem) e.getWeapon().getItem()).getBaseAbility()),
	WEAPON_SPECIAL(3, e -> e.getWeapon() == null ? null : ((WeaponItem) e.getWeapon().getItem()).getSpecialAbility()),
	ACCESSORY1_SPECIAL(4, e -> e.getAccessory1() == null ? null : ((AccessoryItem) e.getAccessory1().getItem()).getAbility()),
	ACCESSORY2_SPECIAL(5, e -> e.getAccessory2() == null ? null : ((AccessoryItem) e.getAccessory2().getItem()).getAbility());

	private int abilityId;
	private Function<Equipment, Ability> abilityGetter;
}
