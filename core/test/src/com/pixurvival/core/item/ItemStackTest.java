package com.pixurvival.core.item;

import org.junit.Assert;
import org.junit.Test;

public class ItemStackTest {

	@Test
	public void addQuantity() {
		Item item = new Item("fake");
		item.setMaxStackSize(10);
		ItemStack itemStack = new ItemStack(item, 5);
		int rest = itemStack.addQuantity(7);
		Assert.assertEquals(10, itemStack.getQuantity());
		Assert.assertEquals(2, rest);
	}

	@Test
	public void removeQuantity() {
		Item item = new Item("fake");
		item.setMaxStackSize(10);
		ItemStack itemStack = new ItemStack(item, 5);
		int removed = itemStack.removeQuantity(15);
		Assert.assertEquals(0, itemStack.getQuantity());
		Assert.assertEquals(5, removed);
	}

}
