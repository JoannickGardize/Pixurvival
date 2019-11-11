package com.pixurvival.core.contentPack;

import java.util.function.Function;

import com.pixurvival.core.util.CaseUtils;
import com.pixurvival.core.util.ReflectionUtils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TranslationKey {
	NAME(element -> CaseUtils.pascalToCamelCase(ReflectionUtils.getSuperClassUnder(element.getClass(), IdentifiedElement.class).getSimpleName()) + "." + element.getName() + ".name"),
	ITEM_DESCRIPTION(element -> "item." + element.getName() + ".description"),
	ITEM_BASE_ABILITY_NAME(element -> "item." + element.getName() + ".baseAbility.name"),
	ITEM_BASE_ABILITY_DESCRIPTION(element -> "item." + element.getName() + ".baseAbility.description"),
	ITEM_SPECIAL_ABILITY_NAME(element -> "item." + element.getName() + ".specialAbility.name"),
	ITEM_SPECIAL_ABILITY_DESCRIPTION(element -> "item." + element.getName() + ".specialAbility.description");

	private Function<IdentifiedElement, String> keyFunction;

	public String getKey(IdentifiedElement element) {
		return keyFunction.apply(element);
	}
}
