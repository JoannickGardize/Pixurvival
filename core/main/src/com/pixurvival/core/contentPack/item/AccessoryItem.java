package com.pixurvival.core.contentPack.item;

import java.util.function.Consumer;

import com.pixurvival.core.livingEntity.ability.ItemAlterationAbility;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.StatFormula;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessoryItem extends EquipableItem {

	private static final long serialVersionUID = 1L;

	private ItemAlterationAbility ability = new ItemAlterationAbility();

	@Override
	public void forEachStatFormula(Consumer<StatFormula> action) {
		ability.forEachStatFormulas(action);
	}

	@Override
	public void forEachAlteration(Consumer<Alteration> action) {
		ability.forEachAlteration(action::accept);
	}
}
