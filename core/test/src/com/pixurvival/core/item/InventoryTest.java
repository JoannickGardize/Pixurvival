package com.pixurvival.core.item;

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
		itemB = new Item("fake 2");
		itemB.setMaxStackSize(1);
		itemC = new Item("fake 3");
		itemC.setMaxStackSize(10);
		inventory = new Inventory(4);
	}

	@Test
	public void smartAdd() {
		Assert.assertTrue(inventory.smartAdd(new ItemStack(itemA, 3)));
		Assert.assertTrue(inventory.smartAdd(new ItemStack(itemB, 1)));
		Assert.assertTrue(inventory.smartAdd(new ItemStack(itemC, 2)));
		Assert.assertTrue(inventory.smartAdd(new ItemStack(itemA, 5)));
		ItemStack overloadA = new ItemStack(itemA, 5);
		Assert.assertFalse(inventory.smartAdd(overloadA));
		Assert.assertTrue(inventory.smartAdd(new ItemStack(itemC, 3)));
		Assert.assertEquals(new ItemStack(itemA, 5), inventory.getSlot(0));
		Assert.assertEquals(new ItemStack(itemB, 1), inventory.getSlot(1));
		Assert.assertEquals(new ItemStack(itemC, 5), inventory.getSlot(2));
		Assert.assertEquals(new ItemStack(itemA, 5), inventory.getSlot(3));
		Assert.assertEquals(3, overloadA.getQuantity());
	}

	@Test
	public void take() {
		inventory.setSlot(0, new ItemStack(itemB, 1));
		inventory.setSlot(1, new ItemStack(itemA, 5));
		inventory.setSlot(2, new ItemStack(itemC, 4));
		inventory.setSlot(3, new ItemStack(itemA, 5));
		ItemStack itemStack = inventory.smartTake(itemA, 7);
		Assert.assertEquals(new ItemStack(itemA, 7), itemStack);
		itemStack = inventory.smartTake(itemB, 2);
		Assert.assertNull(itemStack);
		Assert.assertEquals(new ItemStack(itemB, 1), inventory.getSlot(0));
		Assert.assertEquals(new ItemStack(itemA, 3), inventory.getSlot(1));
		Assert.assertEquals(new ItemStack(itemC, 4), inventory.getSlot(2));
		Assert.assertNull(inventory.getSlot(3));
	}
}
