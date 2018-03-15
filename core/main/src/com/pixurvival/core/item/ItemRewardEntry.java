package com.pixurvival.core.item;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.pixurvival.core.contentPack.ElementReference;
import com.pixurvival.core.contentPack.RefContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ItemRewardEntry {

	private ItemStack itemStack;
	private double probability;

	@Getter
	public static class XmlEntry {
		@XmlAttribute(name = "packRef")
		private String packRef;
		@XmlAttribute(name = "name")
		private String itemName;
		@XmlAttribute(name = "quantity")
		private int quantity = 1;
		@XmlAttribute(name = "probability")
		private double probability = 1;
	}

	@AllArgsConstructor
	public static class Adapter extends XmlAdapter<XmlEntry, ItemRewardEntry> {

		private RefContext refContext;

		@Override
		public ItemRewardEntry unmarshal(XmlEntry v) throws Exception {
			ItemRewardEntry itemRewardEntry = new ItemRewardEntry();
			ElementReference ref = new ElementReference(v.getPackRef(), v.getItemName());
			itemRewardEntry.itemStack = new ItemStack(refContext.get(Item.class, ref), v.getQuantity());
			itemRewardEntry.probability = v.getProbability();
			return itemRewardEntry;
		}

		@Override
		public XmlEntry marshal(ItemRewardEntry v) throws Exception {
			// TODO
			return null;
		}

	}

}
