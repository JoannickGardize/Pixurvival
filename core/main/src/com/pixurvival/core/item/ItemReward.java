package com.pixurvival.core.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pixurvival.core.contentPack.NamedElement;

public class ItemReward extends NamedElement {

	private static ThreadLocal<List<ItemStack>> tmpLists = ThreadLocal.withInitial(() -> new ArrayList<>());

	@XmlElement(name = "item")
	@XmlJavaTypeAdapter(ItemRewardEntry.XmlEntryAdapter.class)
	private ItemRewardEntry[] entries;

	public ItemStack[] produce(Random random) {
		List<ItemStack> result = tmpLists.get();
		result.clear();
		for (ItemRewardEntry entry : entries) {
			if (random.nextDouble() <= entry.getProbability()) {
				result.add(entry.getItemStack().copy());
			}
		}
		return result.toArray(new ItemStack[result.size()]);
	}
}
