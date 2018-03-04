package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pixurvival.core.item.Inventory;

public class InventoryTable extends Table {

	private Inventory inventory;
	private int rowLength;

	public InventoryTable(Inventory inventory, int rowLength) {
		this.inventory = inventory;
		this.rowLength = rowLength;

		int rowCount = inventory.getSize() / rowLength;
		if (inventory.getSize() % rowLength > 0) {
			rowCount++;
		}
		defaults().fill().expand().pad(2).size(30);
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < rowLength; j++) {
				add(new InventorySlot(inventory, i * rowLength + j));
			}
			row();
		}
		pack();
	}
}
