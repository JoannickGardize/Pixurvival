package com.pixurvival.core.message;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerInventory;
import com.pixurvival.core.util.Vector2;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
public class CreateWorld {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemIdStack {
        private int item;
        private int quantity;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Inventory {
        ItemIdStack[] items;
    }

    private long id;
    private ContentPackIdentifier contentPackIdentifier;
    private int gameModeId;
    private long myPlayerId;
    private long myOriginalPlayerId;
    private Vector2 myPosition;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Inventory inventory;
    private TeamComposition[] teamCompositions;
    private long[] playerDeadIds;
    private int myTeamId;
    private boolean spectator;
    private Vector2 mySpawnCenter;
    private Vector2 worldSpawnCenter;
    private int[] discoveredItemCrafts;
    private Map<String, Object> customData = new HashMap<>();

    private transient PlayerInventory playerInventory;

    public void setInventory(PlayerInventory playerInventory) {
        ItemIdStack[] items = new ItemIdStack[playerInventory.size() + 1];
        for (int i = 0; i < playerInventory.size(); i++) {
            items[i] = map(playerInventory.getSlot(i));
        }
        items[items.length - 1] = map(playerInventory.getHeldItemStack());
        inventory = new Inventory(items);
    }

    public PlayerInventory getInventory(ContentPack contentPack) {
        if (playerInventory != null) {
            return playerInventory;
        }
        if (inventory == null) {
            return null;
        }
        playerInventory = new PlayerInventory(inventory.getItems().length - 1);
        for (int i = 0; i < playerInventory.size(); i++) {
            playerInventory.setSlot(i, map(inventory.getItems()[i], contentPack));
        }
        playerInventory.setHeldItemStack(map(inventory.getItems()[inventory.getItems().length - 1], contentPack));
        return playerInventory;
    }

    public ItemIdStack map(ItemStack itemStack) {
        return itemStack == null ? null : new ItemIdStack(itemStack.getItem().getId(), itemStack.getQuantity());
    }

    public ItemStack map(ItemIdStack itemIdStack, ContentPack contentPack) {
        return itemIdStack == null ? null : new ItemStack(contentPack.getItems().get(itemIdStack.getItem()), itemIdStack.getQuantity());
    }
}
