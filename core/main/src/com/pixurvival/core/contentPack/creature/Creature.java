package com.pixurvival.core.contentPack.creature;

import java.util.function.Consumer;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.StatFormula;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Creature extends IdentifiedElement {

	private static final AbilitySet EMPTY_ABILITY_SET = new AbilitySet();

	static {
		EMPTY_ABILITY_SET.addSilence();
	}

	private static final long serialVersionUID = 1L;

	@Required
	@ElementReference
	private SpriteSheet spriteSheet;

	@Bounds(min = 0)
	private float collisionRadius;

	private float strength;
	private float agility;
	private float intelligence;

	private boolean solid = true;

	@Required
	@ElementReference
	private BehaviorSet behaviorSet;

	@ElementReference
	private ItemReward itemReward;

	@ElementReference
	private AbilitySet abilitySet;

	private long lifetime;

	@Override
	public void initialize() {
		if (abilitySet == null) {
			abilitySet = EMPTY_ABILITY_SET;
		} else {
			abilitySet.addSilence();
		}
	}

	@Override
	public void forEachStatFormula(Consumer<StatFormula> action) {
		if (abilitySet != null) {
			abilitySet.forEachStatFormula(action);
		}
	}

	@Override
	public void forEachAlteration(Consumer<Alteration> action) {
		if (abilitySet != null) {
			abilitySet.forEachAlteration(action);
		}
	}
}
