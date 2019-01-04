package com.pixurvival.contentPackEditor.component.item;

import java.util.HashMap;
import java.util.Map;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.core.item.Item;
import com.pixurvival.core.item.Item.Details;
import com.pixurvival.core.util.CaseUtils;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public enum ItemType {
	RESOURCE(new ResourceEditor()),
	CLOTHING(new ClothingEditor()),
	ACCESSORY(new AccessoryEditor()),
	EDIBLE(new EdibleEditor()),
	MELEE_WEAPON(new MeleeWeaponEditor()),
	RANGED_WEAPON(new RangedWeaponEditor()),
	STRUCTURE(new StructureEditor());

	private static Map<Class<?>, ItemType> itemTypeByClass = new HashMap<>();

	static {
		for (ItemType itemType : ItemType.values()) {
			itemType.label = TranslationService.getInstance().getString("item.type." + CaseUtils.upperToCamelCase(itemType.name()));
		}
		for (Class<?> classType : Item.class.getClasses()) {
			try {
				ItemType itemType = ItemType.valueOf(CaseUtils.camelToUpperCase(classType.getSimpleName()));
				itemType.detailsType = (Class<? extends Details>) classType;
				itemTypeByClass.put(classType, itemType);
			} catch (IllegalArgumentException e) {
			}
		}

	}

	private String label;

	private @NonNull ElementEditor<? extends Details> detailsEditor;

	private Class<? extends Details> detailsType;

	@Override
	public String toString() {
		return label;
	}

	public static ItemType forClass(Class<? extends Details> clazz) {
		return itemTypeByClass.get(clazz);
	}
}
