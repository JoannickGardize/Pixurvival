package com.pixurvival.core.contentPack.item;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.pixurvival.core.item.EdibleItem;
import com.pixurvival.core.item.EquipableItem;
import com.pixurvival.core.item.Item;
import com.pixurvival.core.item.MeleeWeaponItem;
import com.pixurvival.core.item.RangedWeaponItem;
import com.pixurvival.core.item.StructureItem;

public class ItemAdapter extends XmlAdapter<BaseItem, Item> {

	@Override
	public Item unmarshal(BaseItem baseItem) throws Exception {
		Item item = null;
		switch (baseItem.getType()) {
		case EDIBLE:
			item = new EdibleItem(baseItem.getName());
			break;
		case EQUIPABLE:
			item = new EquipableItem(baseItem.getName());
			break;
		case MELEE_WEAPON:
			item = new MeleeWeaponItem(baseItem.getName());
			break;
		case RANGED_WEAPON:
			item = new RangedWeaponItem(baseItem.getName());
			break;
		case RESOURCE:
			item = new Item(baseItem.getName());
			break;
		case STRUCTURE:
			item = new StructureItem(baseItem.getName());
		default:
			break;
		}
		item.setFrame(baseItem.getFrame());
		item.setMaxStackSize(baseItem.getStackSize());
		return item;
	}

	@Override
	public BaseItem marshal(Item v) throws Exception {
		// TODO
		return null;
	}

}