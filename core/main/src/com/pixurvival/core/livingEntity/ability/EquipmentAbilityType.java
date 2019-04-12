package com.pixurvival.core.livingEntity.ability;

import java.util.function.Function;

import com.pixurvival.core.item.Item.Accessory;
import com.pixurvival.core.item.Item.Weapon;
import com.pixurvival.core.livingEntity.Equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EquipmentAbilityType {
	WEAPON_BASE(e -> e.getWeapon() == null ? null : ((Weapon) e.getWeapon().getItem().getDetails()).getBaseAbility()),
	WEAPON_SPECIAL(e -> e.getWeapon() == null ? null : ((Weapon) e.getWeapon().getItem().getDetails()).getSpecialAbility()),
	ACCESSORY1_SPECIAL(e -> e.getAccessory1() == null ? null : ((Accessory) e.getAccessory1().getItem().getDetails()).getSpecialAbility()),
	ACCESSORY2_SPECIAL(e -> e.getAccessory2() == null ? null : ((Accessory) e.getAccessory2().getItem().getDetails()).getSpecialAbility());

	private Function<Equipment, Ability> abilityGetter;
}
