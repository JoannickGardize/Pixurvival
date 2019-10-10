package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;

public class StatusUI extends Table {

	public StatusUI() {
		defaults().fill().size(50, 50).pad(2);
		add(new AbilityCooldownBox(EquipmentAbilityType.WEAPON_BASE));
		add(new AbilityCooldownBox(EquipmentAbilityType.WEAPON_SPECIAL));
		add(new AbilityCooldownBox(EquipmentAbilityType.ACCESSORY1_SPECIAL));
		add(new AbilityCooldownBox(EquipmentAbilityType.ACCESSORY2_SPECIAL));
		pack();
		setColor(1, 1, 1, 0.5f);
	}

	public void updatePosition() {
		setPosition(Gdx.graphics.getWidth() / 2f - getWidth() / 2, 30);
	}
}
