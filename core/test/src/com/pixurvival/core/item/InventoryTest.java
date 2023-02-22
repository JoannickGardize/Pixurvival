package com.pixurvival.core.item;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ResourceItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    void add() {
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
    void add2() {
        Assertions.assertNull(inventory.add(new ItemStack(itemB, 3)));
        Assertions.assertEquals(new ItemStack(itemB, 1), inventory.getSlot(0));
        Assertions.assertEquals(new ItemStack(itemB, 1), inventory.getSlot(1));
        Assertions.assertEquals(new ItemStack(itemB, 1), inventory.getSlot(2));
        Assertions.assertNull(inventory.getSlot(3));
    }

    @Test
    void addAllTest() {
        ItemStack itemStackB = new ItemStack(itemB);
        inventory.setSlot(0, itemStackB);
        inventory.setSlot(1, itemStackB);
        inventory.setSlot(3, itemStackB);
        Assertions.assertFalse(inventory.addAllOrFail(Arrays.asList(itemStackB, new ItemStack(itemA))));
        assertContent(inventory, itemStackB, itemStackB, null, itemStackB);
        inventory.setSlot(3, new ItemStack(itemA, 4));
        Assertions.assertTrue(inventory.addAllOrFail(Arrays.asList(itemStackB, new ItemStack(itemA))));
        assertContent(inventory, itemStackB, itemStackB, itemStackB, new ItemStack(itemA, 5));
        inventory.setSlot(2, null);
        inventory.setSlot(3, null);
        Assertions.assertTrue(inventory.addAllOrFail(Arrays.asList(new ItemStack(itemA), new ItemStack(itemC))));
        assertContent(inventory, itemStackB, itemStackB, new ItemStack(itemA), new ItemStack(itemC));
    }

    @Test
    void remove() {
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

    @Test
    void serializationTest() {
        Inventory inventory = new Inventory(10);
        inventory.setSlot(4, new ItemStack(itemA, 3));
        inventory.setSlot(5, new ItemStack(itemA, 2));
        inventory.setSlot(8, new ItemStack(itemB, 1));
        ContentPack contentPack = new ContentPack();
        itemA.setId(0);
        itemB.setId(1);
        contentPack.getItems().add(itemA);
        contentPack.getItems().add(itemB);
        ByteBuffer buffer = ByteBuffer.allocate(128);
        inventory.write(buffer);
        buffer.flip();
        Inventory copy = new Inventory(10);
        copy.apply(contentPack, buffer);
        Assertions.assertEquals(inventory, copy);
    }

    private void assertContent(Inventory inventory, ItemStack... content) {
        for (int i = 0; i < inventory.size(); i++) {
            Assertions.assertEquals(content[i], inventory.getSlot(i));
        }
    }
}
