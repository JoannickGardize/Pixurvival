package com.pixurvival.core.contentPack;

import com.pixurvival.core.util.CaseUtils;
import com.pixurvival.core.util.ReflectionUtils;
import lombok.AllArgsConstructor;

import java.util.function.Function;

@AllArgsConstructor
public enum TranslationKey {
    NAME(element -> getElementTypeKeyPreffix(element) + element.getName() + ".name"),
    DESCRIPTION(element -> getElementTypeKeyPreffix(element) + element.getName() + ".description"),
    ITEM_BASE_ABILITY_NAME(element -> "item." + element.getName() + ".baseAbility.name"),
    ITEM_BASE_ABILITY_DESCRIPTION(element -> "item." + element.getName() + ".baseAbility.description"),
    ITEM_SPECIAL_ABILITY_NAME(element -> "item." + element.getName() + ".specialAbility.name"),
    ITEM_SPECIAL_ABILITY_DESCRIPTION(element -> "item." + element.getName() + ".specialAbility.description");

    private Function<NamedIdentifiedElement, String> keyFunction;

    public String getKey(NamedIdentifiedElement element) {
        return keyFunction.apply(element);
    }

    private static String getElementTypeKeyPreffix(NamedIdentifiedElement element) {
        return CaseUtils.pascalToCamelCase(ReflectionUtils.getSuperClassUnder(element.getClass(), NamedIdentifiedElement.class, TaggableElement.class).getSimpleName()) + ".";
    }
}
