package com.pixurvival.core.contentPack;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.pixurvival.core.item.Item;

public class ItemAdapter extends XmlAdapter<BaseItem, Item> {

	@Override
	public Item unmarshal(BaseItem baseItem) throws Exception {
		Item item = null;
		switch (baseItem.getType()) {
		case EDIBLE:
			break;
		case EQUIPABLE:
			break;
		case MELEE_WEAPON:
			break;
		case RANGED_WEAPON:
			break;
		case RESOURCE:
			item = new Item();
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	public BaseItem marshal(Item v) throws Exception {
		// TODO
		return null;
	}

}
