package com.pixurvival.core.item;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InventoryTest {

	private Item itemA;
	private Item itemB;
	private Item itemC;
	private Inventory inventory;

	@Before
	public void beforeTest() {
		itemA = new Item("fake 1");
		itemA.setMaxStackSize(5);
		itemA.setId((short) 0);
		itemB = new Item("fake 2");
		itemB.setMaxStackSize(1);
		itemB.setId((short) 1);
		itemC = new Item("fake 3");
		itemC.setMaxStackSize(10);
		itemC.setId((short) 2);
		inventory = new Inventory(4);
	}

	@Test
	public void add() {
		Assert.assertNull(inventory.add(new ItemStack(itemA, 3)));
		Assert.assertNull(inventory.add(new ItemStack(itemB, 1)));
		Assert.assertNull(inventory.add(new ItemStack(itemC, 2)));
		Assert.assertNull(inventory.add(new ItemStack(itemA, 5)));
		ItemStack overloadA = new ItemStack(itemA, 5);
		Assert.assertEquals(new ItemStack(itemA, 3), inventory.add(overloadA));
		Assert.assertNull(inventory.add(new ItemStack(itemC, 3)));
		Assert.assertEquals(new ItemStack(itemA, 5), inventory.getSlot(0));
		Assert.assertEquals(new ItemStack(itemB, 1), inventory.getSlot(1));
		Assert.assertEquals(new ItemStack(itemC, 5), inventory.getSlot(2));
		Assert.assertEquals(new ItemStack(itemA, 5), inventory.getSlot(3));
	}

	@Test
	public void remove() {
		inventory.setSlot(0, new ItemStack(itemB, 1));
		inventory.setSlot(1, new ItemStack(itemA, 5));
		inventory.setSlot(2, new ItemStack(itemC, 4));
		inventory.setSlot(3, new ItemStack(itemA, 5));

		ItemStack itemStack = new ItemStack(itemA, 7);
		List<ItemStack> list = new ArrayList<>();
		list.add(itemStack);
		Assert.assertTrue(inventory.remove(list));
		itemStack = new ItemStack(itemB, 2);
		list.set(0, itemStack);
		Assert.assertFalse(inventory.remove(list));

		Assert.assertEquals(new ItemStack(itemB, 1), inventory.getSlot(0));
		Assert.assertEquals(new ItemStack(itemA, 3), inventory.getSlot(1));
		Assert.assertEquals(new ItemStack(itemC, 4), inventory.getSlot(2));
		Assert.assertNull(inventory.getSlot(3));
	}
}
