package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pixurvival.core.item.Inventory;

public class InventoryTable extends Table {

	public InventoryTable(Inventory inventory, int rowLength) {
		int size = inventory.size();
		int rowCount = size / rowLength;
		if (size % rowLength > 0) {
			rowCount++;
		}
		defaults().fill().expand().size(30);
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < rowLength; j++) {
				int index = i * rowLength + j;
				if (index < size) {
					addSlot(inventory, index);
				}
			}
			row();
		}
		pack();
	}

	public void addSlot(Inventory inventory, int index) {
		add(new InventorySlot(inventory, index));
	}
}
