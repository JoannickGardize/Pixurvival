package com.pixurvival.gdxcore.ui;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.item.Inventory;

public class CraftTable extends Table {

	private int rowCount = 1;
	private int rowLength;
	private int currentSize;

	public CraftTable(List<ItemCraft> itemCrafts, int rowLength) {
		this.rowLength = rowLength;
		defaults().fill().minSize(10).maxSize(60).prefSize(60).padLeft(-1).padTop(-1);
		for (ItemCraft itemCraft : itemCrafts) {
			addSlot(itemCraft, false);
		}
	}

	public void addSlot(ItemCraft itemCraft, boolean newlyDiscovered) {
		add(new CraftSlot(itemCraft, newlyDiscovered));
		currentSize++;
		if (currentSize % rowLength == 0) {
			row();
			rowCount++;
		}
	}

	public void append(List<ItemCraft> itemCrafts) {
		for (int i = currentSize; i < itemCrafts.size(); i++) {
			addSlot(itemCrafts.get(i), true);
		}
	}

	@Override
	public void layout() {
		sizeToFill(getWidth(), getHeight());
		super.layout();
	}

	@SuppressWarnings("unchecked")
	public void sizeToFill(float width, float height) {
		float slotSize = Math.min(width / rowLength, height / rowCount);
		for (Cell<Actor> cell : getCells()) {
			cell.prefSize(slotSize);
		}
	}

	/**
	 * Allow override of slots actor type
	 * 
	 * @param inventory
	 * @param index
	 * @return
	 */
	public Actor newSlot(Inventory inventory, int index) {
		return new InventorySlot(inventory, index);
	}
}