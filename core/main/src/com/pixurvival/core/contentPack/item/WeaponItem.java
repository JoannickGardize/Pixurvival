package com.pixurvival.core.contentPack.item;

import java.util.function.Consumer;

import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.AnimationTemplateRequirement;
import com.pixurvival.core.contentPack.validation.annotation.AnimationTemplateRequirementSet;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.ability.ItemAlterationAbility;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.StatFormula;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeaponItem extends EquipableItem {

	private static final long serialVersionUID = 1L;

	@ElementReference
	@AnimationTemplateRequirement(AnimationTemplateRequirementSet.CHARACTER)
	private SpriteSheet spriteSheet;

	@Valid
	private ItemAlterationAbility baseAbility = new ItemAlterationAbility();

	@Valid
	private ItemAlterationAbility specialAbility = new ItemAlterationAbility();

	@Override
	public void forEachStatFormula(Consumer<StatFormula> action) {
		baseAbility.forEachStatFormulas(action);
		specialAbility.forEachStatFormulas(action);
	}

	@Override
	public void forEachAlteration(Consumer<Alteration> action) {
		baseAbility.forEachAlteration(action::accept);
		specialAbility.forEachAlteration(action::accept);
	}
}
