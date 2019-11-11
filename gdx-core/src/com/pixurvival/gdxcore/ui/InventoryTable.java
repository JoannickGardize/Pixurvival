package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pixurvival.core.item.Inventory;

import lombok.Getter;

public class InventoryTable extends Table {

	private @Getter int rowCount;
	private @Getter int rowLength;

	public InventoryTable(Inventory inventory, int rowLength) {
		this.rowLength = rowLength;
		int size = inventory.size();
		rowCount = size / rowLength;
		if (size % rowLength > 0) {
			rowCount++;
		}
		defaults().fill().minSize(10).maxSize(60).prefSize(60).padLeft(-1).padTop(-1);
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < rowLength; j++) {
				int index = i * rowLength + j;
				if (index < size) {
					add(newSlot(inventory, index));
				}
			}
			row();
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
