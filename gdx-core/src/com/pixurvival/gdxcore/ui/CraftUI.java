package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pixurvival.core.contentPack.item.*;
import com.pixurvival.core.util.CaseUtils;
import com.pixurvival.gdxcore.PixurvivalGame;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;

public class CraftUI extends UIWindow {

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
        Actor title;
        Cell<CraftGroup> tableCell;
    }

    private Map<CraftCategory, CategoryEntry> sortedItemCrafts = new EnumMap<>(CraftCategory.class);

    public CraftUI() {
        super("crafting");
        Table table = new Table();
        table.defaults().pad(2);
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
            table.add(entry.getValue().title).expandX();
            table.row();
            List<ItemCraft> categoryList = entry.getValue().itemCrafts;
            entry.getValue().tableCell = table.add(new CraftGroup(categoryList)).expandX().fill();
            table.row();
        }
        table.add().expand();
        ScrollPane scrollPane = new ScrollPane(table, PixurvivalGame.getSkin());
        scrollPane.setScrollingDisabled(true, false);
        add(scrollPane).expand().fill();
    }

    public void addItemCrafts(Collection<ItemCraft> itemCrafts) {
        for (ItemCraft itemCraft : itemCrafts) {
            CraftCategory category = CraftCategory.of(itemCraft.getResult().getItem());
            CategoryEntry entry = sortedItemCrafts.get(category);
            entry.itemCrafts.add(itemCraft);
            entry.tableCell.getActor().addSlot(itemCraft, true);
        }
    }

}
