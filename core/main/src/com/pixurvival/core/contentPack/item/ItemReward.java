package com.pixurvival.core.contentPack.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.item.ItemStack;

import lombok.Data;
import lombok.Getter;

public class ItemReward extends NamedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	@Data
	public static class Entry implements Serializable {

		private static final long serialVersionUID = 1L;

		private ItemStack itemStack;
		private double probability;
	}

	private static ThreadLocal<List<ItemStack>> tmpLists = ThreadLocal.withInitial(() -> new ArrayList<>());

	private @Getter List<Entry> entries = new ArrayList<>();

	public ItemStack[] produce(Random random) {
		List<ItemStack> result = tmpLists.get();
		result.clear();
		for (Entry entry : entries) {
			if (random.nextDouble() <= entry.getProbability()) {
				result.add(entry.getItemStack());
			}
		}
		return result.toArray(new ItemStack[result.size()]);
	}
}
