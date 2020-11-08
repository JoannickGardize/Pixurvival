package com.pixurvival.gdxcore.ui;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pixurvival.core.contentPack.item.AccessoryItem;
import com.pixurvival.core.contentPack.item.ClothingItem;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.item.StructureItem;
import com.pixurvival.core.contentPack.item.WeaponItem;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.util.CaseUtils;
import com.pixurvival.gdxcore.PixurvivalGame;

import lombok.AllArgsConstructor;

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

	public CraftUI() {
		super("crafting");
		Table table = new Table();
		table.defaults().pad(2);
		List<ItemCraft> itemCrafts = PixurvivalGame.getWorld().getContentPack().getItemCrafts();
		Map<CraftCategory, List<ItemCraft>> sortedItemCrafts = new EnumMap<>(CraftCategory.class);
		for (CraftCategory category : CraftCategory.values()) {
			sortedItemCrafts.put(category, new ArrayList<>());
		}
		for (ItemCraft itemCraft : itemCrafts) {
			sortedItemCrafts.get(CraftCategory.of(itemCraft.getResult().getItem())).add(itemCraft);
		}
		for (Entry<CraftCategory, List<ItemCraft>> entry : sortedItemCrafts.entrySet()) {
			table.add(new Label(entry.getKey().getTitle(), PixurvivalGame.getSkin(), "white"));
			table.row();
			List<ItemCraft> categoryList = entry.getValue();
			Inventory inventory = new Inventory(categoryList.size());
			table.add(new InventoryTable(inventory, 8) {
				@Override
				public Actor newSlot(Inventory inventory, int index) {
					return new CraftSlot(categoryList.get(index));
				}
			}).fill().prefHeight(60f + ((categoryList.size() - 1f) / 8f) * 60f);
			table.row();
		}
		table.add().expand();
		ScrollPane scrollPane = new ScrollPane(table, PixurvivalGame.getSkin());
		scrollPane.setScrollingDisabled(true, false);
		add(scrollPane).expand().fill();

		pack();
	}
}
