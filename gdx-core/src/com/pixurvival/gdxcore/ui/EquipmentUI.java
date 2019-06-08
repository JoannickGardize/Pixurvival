package com.pixurvival.gdxcore.ui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.livingEntity.Equipment;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.gdxcore.PixurvivalGame;

public class EquipmentUI extends UIWindow {

	private Label strengthLabel;
	private Label agilityLabel;
	private Label intelligenceLabel;

	private DecimalFormat decimalFormat = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.US));

	public EquipmentUI() {
		super("equipment");
		Inventory inventory = new Inventory(4);
		InventoryTable equipmentTable = new InventoryTable(inventory, 4) {
			@Override
			public Actor newSlot(Inventory inventory, int index) {
				switch (index) {
				case 0:
					return new EquipmentSlot(Equipment.CLOTHING_INDEX);
				case 1:
					return new EquipmentSlot(Equipment.WEAPON_INDEX);
				case 2:
					return new EquipmentSlot(Equipment.ACCESSORY1_INDEX);
				case 3:
					return new EquipmentSlot(Equipment.ACCESSORY2_INDEX);
				default:
					return null;
				}
			}
		};

		Table statTable = new Table();
		statTable.defaults().fill().align(Align.right).minWidth(40);
		statTable.add(new UILabel("statType.strength", " ", Color.RED));
		strengthLabel = new UILabel(Color.RED);
		statTable.add(strengthLabel);
		statTable.row();
		statTable.add(new UILabel("statType.agility", " ", Color.GREEN));
		agilityLabel = new UILabel(Color.GREEN);
		statTable.add(agilityLabel);
		statTable.row();
		statTable.add(new UILabel("statType.intelligence", " ", Color.BLUE));
		intelligenceLabel = new UILabel(Color.BLUE);
		statTable.add(intelligenceLabel);
		statTable.row();

		Table mainTable = new Table();
		mainTable.add(equipmentTable).expand().fill();
		mainTable.add(statTable).expand().fill();
		add(mainTable).expand().fill();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (PixurvivalGame.getClient().getMyPlayer() != null) {
			StatSet stats = PixurvivalGame.getClient().getMyPlayer().getStats();
			strengthLabel.setText(decimalFormat.format(stats.getValue(StatType.STRENGTH)));
			agilityLabel.setText(decimalFormat.format(stats.getValue(StatType.AGILITY)));
			intelligenceLabel.setText(decimalFormat.format(stats.getValue(StatType.INTELLIGENCE)));
		}
		super.draw(batch, parentAlpha);
	}

}
