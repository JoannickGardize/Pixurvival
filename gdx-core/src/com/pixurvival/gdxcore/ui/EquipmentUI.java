package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pixurvival.core.aliveEntity.Equipment;

public class EquipmentUI extends UIWindow {

	public EquipmentUI() {
		super("character");
		Table table = new Table();
		table.defaults().pad(10).fill().expand().size(30);
		table.add(new EquipmentSlot(Equipment.CLOTHING_INDEX));
		table.add(new EquipmentSlot(Equipment.WEAPON_INDEX));
		table.row();
		table.add(new EquipmentSlot(Equipment.ACCESSORY1_INDEX));
		table.add(new EquipmentSlot(Equipment.ACCESSORY2_INDEX));
		add(table);
		pack();
	}

}
