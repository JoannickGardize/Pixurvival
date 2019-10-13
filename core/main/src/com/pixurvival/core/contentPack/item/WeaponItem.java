package com.pixurvival.core.contentPack.item;

import com.pixurvival.core.livingEntity.ability.AlterationAbility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeaponItem extends EquipableItem {

	private static final long serialVersionUID = 1L;

	private AlterationAbility baseAbility;
	private AlterationAbility specialAbility;

}
