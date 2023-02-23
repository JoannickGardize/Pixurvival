package com.pixurvival.core.item;

import com.pixurvival.core.contentPack.elementSet.ElementSet;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.util.FloatComparison;

public class InventoryUtils {

    public static boolean testContent(InventoryHolder entity, ElementSet<Item> itemFilter, FloatComparison operator, int testSum) {
        Inventory inventory = entity.getInventory();
        int sum = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getSlot(i);
            if (itemStack != null && itemFilter.contains(itemStack.getItem())) {
                sum += itemStack.getQuantity();
            }
        }
        return operator.test(sum, testSum);
    }
}
