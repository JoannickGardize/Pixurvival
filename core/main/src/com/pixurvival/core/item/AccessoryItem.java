package com.pixurvival.core.item;

import com.pixurvival.core.livingEntity.ability.EffectAbility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessoryItem extends EquipableItem {

	private static final long serialVersionUID = 1L;

	private EffectAbility ability;

}
