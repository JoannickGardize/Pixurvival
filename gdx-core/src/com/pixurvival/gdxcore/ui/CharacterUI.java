package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class CharacterUI extends UIWindow {

	public CharacterUI() {
		super("character");
		Table table = new Table();

		table.defaults().expand().fill();
		EquipmentUI equipmentUI = new EquipmentUI();
		equipmentUI.setMovable(false);
		table.add(equipmentUI);
		table.row();
		InventoryUI inventoryUI = new InventoryUI();
		inventoryUI.setMovable(false);
		table.add(inventoryUI);
		table.row();
		CraftUI craftUI = new CraftUI();
		craftUI.setMovable(false);
		table.add(craftUI);

		add(table).expand().fill();
		this.pack();
	}

}
