package com.pixurvival.core.contentPack.item;

import com.pixurvival.core.livingEntity.ability.ItemAlterationAbility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessoryItem extends EquipableItem {

	private static final long serialVersionUID = 1L;

	private ItemAlterationAbility ability;

}
