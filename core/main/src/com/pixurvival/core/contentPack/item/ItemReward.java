package com.pixurvival.core.contentPack.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.item.ItemStack;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class ItemReward extends IdentifiedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	@Data
	public static class Entry implements Serializable {

		private static final long serialVersionUID = 1L;

		@Valid
		@Required
		private ItemStack itemStack = new ItemStack();

		@Bounds(min = 0, max = 1, maxInclusive = true)
		private double probability = 1;
	}

	private static ThreadLocal<List<ItemStack>> tmpLists = ThreadLocal.withInitial(ArrayList::new);

	@Valid
	private @Getter @Setter List<Entry> entries = new ArrayList<>();

	public ItemStack[] produce(Random random) {
		List<ItemStack> result = tmpLists.get();
		result.clear();
		for (Entry entry : entries) {
			if (random.nextDouble() <= entry.getProbability()) {
				result.add(new ItemStack(entry.getItemStack()));
			}
		}
		return result.toArray(new ItemStack[result.size()]);
	}

}
