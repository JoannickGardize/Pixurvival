package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pixurvival.core.contentPack.ItemType.ItemTypeAdapter;
import com.pixurvival.core.util.BeanUtils;

@XmlJavaTypeAdapter(ItemTypeAdapter.class)
public enum ItemType {
	RESOURCE,
	EQUIPABLE,
	EDIBLE,
	MELEE_WEAPON,
	RANGED_WEAPON;

	public static ItemType fromValue(String v) {
		return ItemType.valueOf(BeanUtils.camelToUpperCase(v));
	}

	public static class ItemTypeAdapter extends XmlAdapter<String, ItemType> {

		@Override
		public ItemType unmarshal(String v) throws Exception {
			return ItemType.fromValue(v);
		}

		@Override
		public String marshal(ItemType v) throws Exception {
			return BeanUtils.upperToCamelCase(v.name());
		}

	}
}
