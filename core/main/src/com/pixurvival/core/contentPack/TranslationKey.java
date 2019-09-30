package com.pixurvival.core.contentPack;

import java.util.function.Function;

import com.pixurvival.core.contentPack.item.Item;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TranslationKey {
	ITEM_NAME(name -> "item." + name + ".name"),
	ITEM_DESCRIPTION(name -> "item." + name + ".description"),
	ITEM_BASE_ABILITY_NAME(name -> "item." + name + ".baseAbility.name"),
	ITEM_BASE_ABILITY_DESCRIPTION(name -> "item." + name + ".baseAbility.description"),
	ITEM_SPECIAL_ABILITY_NAME(name -> "item." + name + ".specialAbility.name"),
	ITEM_SPECIAL_ABILITY_DESCRIPTION(name -> "item." + name + ".specialAbility.description");

	private Function<String, String> keyFunction;

	public String getKey(Item item) {
		return keyFunction.apply(item.getName());
	}
}
