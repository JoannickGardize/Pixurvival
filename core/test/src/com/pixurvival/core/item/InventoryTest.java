package com.pixurvival.core.item;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ResourceItem;

public class InventoryTest {

	private Item itemA;
	private Item itemB;
	private Item itemC;
	private Inventory inventory;

	@BeforeEach
	public void beforeTest() {
		itemA = new ResourceItem("fake 1", 0);
		itemA.setMaxStackSize(5);
		itemA.setId((short) 0);
		itemB = new ResourceItem("fake 2", 1);
		itemB.setMaxStackSize(1);
		itemB.setId((short) 1);
		itemC = new ResourceItem("fake 3", 2);
		itemC.setMaxStackSize(10);
		itemC.setId((short) 2);
		inventory = new Inventory(4);
	}

	@Test
	public void add() {
		Assertions.assertNull(inventory.add(new ItemStack(itemA, 3)));
		Assertions.assertNull(inventory.add(new ItemStack(itemB, 1)));
		Assertions.assertNull(inventory.add(new ItemStack(itemC, 2)));
		Assertions.assertNull(inventory.add(new ItemStack(itemA, 5)));
		ItemStack overloadA = new ItemStack(itemA, 5);
		Assertions.assertEquals(new ItemStack(itemA, 3), inventory.add(overloadA));
		Assertions.assertNull(inventory.add(new ItemStack(itemC, 3)));
		Assertions.assertEquals(new ItemStack(itemA, 5), inventory.getSlot(0));
		Assertions.assertEquals(new ItemStack(itemB, 1), inventory.getSlot(1));
		Assertions.assertEquals(new ItemStack(itemC, 5), inventory.getSlot(2));
		Assertions.assertEquals(new ItemStack(itemA, 5), inventory.getSlot(3));
	}

	@Test
	public void add2() {
		Assertions.assertNull(inventory.add(new ItemStack(itemB, 3)));
		Assertions.assertEquals(new ItemStack(itemB, 1), inventory.getSlot(0));
		Assertions.assertEquals(new ItemStack(itemB, 1), inventory.getSlot(1));
		Assertions.assertEquals(new ItemStack(itemB, 1), inventory.getSlot(2));
		Assertions.assertNull(inventory.getSlot(3));
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
		Assertions.assertTrue(inventory.remove(list));
		itemStack = new ItemStack(itemB, 2);
		list.set(0, itemStack);
		Assertions.assertFalse(inventory.remove(list));

		Assertions.assertEquals(new ItemStack(itemB, 1), inventory.getSlot(0));
		Assertions.assertEquals(new ItemStack(itemA, 3), inventory.getSlot(1));
		Assertions.assertEquals(new ItemStack(itemC, 4), inventory.getSlot(2));
		Assertions.assertNull(inventory.getSlot(3));
	}
}
