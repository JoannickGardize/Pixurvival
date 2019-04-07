package com.pixurvival.core.livingEntity.ability;

import java.util.function.Function;

import com.pixurvival.core.item.Item.Accessory;
import com.pixurvival.core.item.Item.Weapon;
import com.pixurvival.core.livingEntity.Equipment;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class EquipmentAbilityProxy extends Ability {

	@Getter
	@AllArgsConstructor
	public enum Type {
		WEAPON_BASE(e -> e.getWeapon() == null ? null : ((Weapon) e.getWeapon().getItem().getDetails()).getBaseAbility()),
		WEAPON_SPECIAL(e -> e.getWeapon() == null ? null : ((Weapon) e.getWeapon().getItem().getDetails()).getSpecialAbility()),
		ACCESSORY1_SPECIAL(e -> e.getAccessory1() == null ? null : ((Accessory) e.getAccessory1().getItem().getDetails()).getSpecialAbility()),
		ACCESSORY2_SPECIAL(e -> e.getAccessory2() == null ? null : ((Accessory) e.getAccessory2().getItem().getDetails()).getSpecialAbility());

		private Function<Equipment, Ability> abilityGetter;
	}

	private Type type;
	private Ability currentAbility;

	@Override
	public boolean start(LivingEntity entity) {
		currentAbility = type.getAbilityGetter().apply(((PlayerEntity) entity).getEquipment());
		return currentAbility != null && currentAbility.start(entity);
	}

	@Override
	public boolean update(LivingEntity entity) {
		return currentAbility.update(entity);
	}

	@Override
	public boolean stop(LivingEntity entity) {
		return currentAbility.stop(entity);
	}
}
