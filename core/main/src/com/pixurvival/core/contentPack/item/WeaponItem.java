package com.pixurvival.core.contentPack.item;

import com.pixurvival.core.livingEntity.ability.EffectAbility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeaponItem extends EquipableItem {

	private static final long serialVersionUID = 1L;

	private EffectAbility baseAbility;
	private EffectAbility specialAbility;

}
