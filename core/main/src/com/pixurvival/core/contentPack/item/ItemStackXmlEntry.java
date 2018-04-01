package com.pixurvival.core.contentPack.item;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.pixurvival.core.contentPack.ElementReference;
import com.pixurvival.core.contentPack.RefContext;
import com.pixurvival.core.item.Item;
import com.pixurvival.core.item.ItemStack;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ItemStackXmlEntry extends ElementReference {
	@XmlAttribute(name = "quantity")
	private int quantity = 1;

	@AllArgsConstructor
	public static class Adapter extends XmlAdapter<ItemStackXmlEntry, ItemStack> {

		private RefContext refContext;

		@Override
		public ItemStack unmarshal(ItemStackXmlEntry v) throws Exception {
			return new ItemStack(refContext.get(Item.class, v), v.getQuantity());
		}

		@Override
		public ItemStackXmlEntry marshal(ItemStack v) throws Exception {
			// TODO
			return null;
		}

	}
}