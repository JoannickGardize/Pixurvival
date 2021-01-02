package com.pixurvival.gdxcore.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pixurvival.core.contentPack.item.AccessoryItem;
import com.pixurvival.core.contentPack.item.ClothingItem;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.item.StructureItem;
import com.pixurvival.core.contentPack.item.WeaponItem;
import com.pixurvival.core.util.CaseUtils;
import com.pixurvival.gdxcore.PixurvivalGame;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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
		Cell<CraftTable> tableCell;
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
			table.add(new Label(entry.getKey().getTitle(), PixurvivalGame.getSkin(), "white"));
			table.row();
			List<ItemCraft> categoryList = entry.getValue().itemCrafts;
			entry.getValue().tableCell = table.add(new CraftTable(categoryList, 8)).fill().prefHeight(60f + ((categoryList.size() - 1f) / 8f) * 60f);
			table.row();
		}
		table.add().expand();
		ScrollPane scrollPane = new ScrollPane(table, PixurvivalGame.getSkin());
		scrollPane.setScrollingDisabled(true, false);
		add(scrollPane).expand().fill();
		pack();
	}

	public void addItemCrafts(Collection<ItemCraft> itemCrafts) {
		Map<CraftCategory, CategoryEntry> changedCategories = new EnumMap<>(CraftCategory.class);
		for (ItemCraft itemCraft : itemCrafts) {
			CraftCategory category = CraftCategory.of(itemCraft.getResult().getItem());
			CategoryEntry entry = sortedItemCrafts.get(category);
			entry.itemCrafts.add(itemCraft);
			changedCategories.put(category, entry);
		}
		for (CategoryEntry entry : changedCategories.values()) {
			entry.tableCell.getActor().append(entry.itemCrafts);
			entry.tableCell.prefHeight(60f + ((entry.itemCrafts.size() - 1f) / 8f) * 60f);
		}
		invalidate();
	}

}
