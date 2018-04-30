package com.pixurvival.core.item;

import lombok.Getter;

@Getter
public class EquipableItem extends Item {

	private float strengthBonus;
	private float agilityBonus;
	private float intelligenceBonus;

	public EquipableItem(String name) {
		super(name);
	}

}
