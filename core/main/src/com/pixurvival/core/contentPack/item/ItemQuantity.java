package com.pixurvival.core.contentPack.item;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.pixurvival.core.contentPack.ElementReference;
import com.pixurvival.core.contentPack.RefContext;
import com.pixurvival.core.item.Item;
import com.pixurvival.core.item.ItemStack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemQuantity {

	private Item item;
	private int quantity;

	public ItemStack toItemStack() {
		return new ItemStack(item, quantity);
	}

	@Getter
	public static class ItemQuantityXmlEntry extends ElementReference {
		@XmlAttribute(name = "quantity")
		private int quantity;
	}

	@AllArgsConstructor
	public static class Adapter extends XmlAdapter<ItemQuantityXmlEntry, ItemQuantity> {

		private RefContext refContext;

		@Override
		public ItemQuantity unmarshal(ItemQuantityXmlEntry v) throws Exception {
			return new ItemQuantity(refContext.get(Item.class, v), v.getQuantity());
		}

		@Override
		public ItemQuantityXmlEntry marshal(ItemQuantity v) throws Exception {
			// TODO
			return null;
		}

	}
}
