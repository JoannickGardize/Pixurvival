package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.core.contentPack.item.*;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryListener;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.util.CaseUtils;
import com.pixurvival.gdxcore.PixurvivalGame;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;

public class CraftUI extends UIWindow implements InventoryListener {

    @AllArgsConstructor
    private enum CraftCategory {
        CLOTHING(i -> i instanceof ClothingItem),
        WEAPONS(i -> i instanceof WeaponItem),
        ACCESSORIES(i -> i instanceof AccessoryItem),
        STRUCTURES(i -> i instanceof StructureItem),
        EDIBLES_AND_OTHERS(i -> true);

        private Predicate<Item> categoryPredicate;

        public String getTitle() {
            return PixurvivalGame.getString("hud.itemCraft.category." + CaseUtils.upperToCamelCase(name()));
        }

        public static CraftCategory of(Item item) {
            for (CraftCategory category : CraftCategory.values()) {
                if (category.categoryPredicate.test(item)) {
                    return category;
                }
            }
            return null;
        }
    }

    @RequiredArgsConstructor
    private static class CategoryEntry {
        @NonNull
        CraftCategory category;
        List<ItemCraft> itemCrafts = new ArrayList<>();
        Label title;
        CraftGroup groupActor;
    }

    private Map<CraftCategory, CategoryEntry> sortedItemCrafts = new EnumMap<>(CraftCategory.class);

    public CraftUI() {
        super("crafting");
        VerticalGroup mainGroup = new VerticalGroup();
        mainGroup.pad(2);
        mainGroup.align(Align.top);
        mainGroup.columnAlign(Align.top);
        mainGroup.fill().expand();
        List<ItemCraft> itemCrafts = PixurvivalGame.getWorld().getContentPack().getItemCrafts();
        for (CraftCategory category : CraftCategory.values()) {
            sortedItemCrafts.put(category, new CategoryEntry(category));
        }
        if (PixurvivalGame.getClient().getWorld().isServer()) {
            for (ItemCraft itemCraft : itemCrafts) {
                if (PixurvivalGame.getClient().getMyPlayer().getItemCraftDiscovery().isDiscovered(itemCraft)) {
                    sortedItemCrafts.get(CraftCategory.of(itemCraft.getResult().getItem())).itemCrafts.add(itemCraft);
                }
            }
        }
        for (Entry<CraftCategory, CategoryEntry> entry : sortedItemCrafts.entrySet()) {
            entry.getValue().title = new Label(entry.getKey().getTitle(), PixurvivalGame.getSkin(), "white");
            entry.getValue().title.setAlignment(Align.center);
            mainGroup.addActor(entry.getValue().title);
            List<ItemCraft> categoryList = entry.getValue().itemCrafts;
            entry.getValue().groupActor = (CraftGroup) new CraftGroup(categoryList).expand().fill();
            mainGroup.addActor(entry.getValue().groupActor);
        }
        ScrollPane scrollPane = new ScrollPane(mainGroup, PixurvivalGame.getSkin());
        scrollPane.setScrollingDisabled(true, false);
        add(scrollPane).expand().fill();
        pack();
    }

    public void addItemCrafts(Collection<ItemCraft> itemCrafts) {
        for (ItemCraft itemCraft : itemCrafts) {
            CraftCategory category = CraftCategory.of(itemCraft.getResult().getItem());
            CategoryEntry entry = sortedItemCrafts.get(category);
            entry.itemCrafts.add(itemCraft);
            entry.groupActor.addSlot(itemCraft, true);
        }
    }

    @Override
    public void slotChanged(Inventory inventory, int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {
        sortedItemCrafts.values().forEach(c -> c.groupActor.updateCraftStates());
    }
}
