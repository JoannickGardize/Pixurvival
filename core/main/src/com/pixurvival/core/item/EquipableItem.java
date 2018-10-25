package com.pixurvival.core.item;

import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class EquipableItem extends Item {

	private static final long serialVersionUID = 1L;

	private float strengthBonus;
	private float agilityBonus;
	private float intelligenceBonus;
	private SpriteSheet spriteSheet;

	public EquipableItem(String name) {
		super(name);
	}
}
