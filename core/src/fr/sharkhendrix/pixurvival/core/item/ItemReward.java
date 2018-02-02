package fr.sharkhendrix.pixurvival.core.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ItemReward {

	@Getter
	@AllArgsConstructor
	public static class Entry {
		private Item item;
		private int quantity;
		private double probability;
	}

	private Entry[] entries;

	public ItemReward(Entry... entries) {
		this.entries = entries;
	}

	public int entryCount() {
		return entries.length;
	}

	public Entry getEntry(int index) {
		return entries[index];
	}

	public List<ItemStack> produce(Random random) {
		List<ItemStack> result = new ArrayList<>();
		for (Entry entry : entries) {
			if (random.nextDouble() <= entry.getProbability()) {
				result.add(new ItemStack(entry.getItem(), entry.getQuantity()));
			}
		}
		return result;
	}
}
