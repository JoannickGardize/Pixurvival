package com.pixurvival.core.contentPack.creature;

import java.util.List;
import java.util.function.Consumer;

import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.alteration.StatFormula;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.AbilityIndexes;
import com.pixurvival.core.contentPack.validation.annotation.AnimationTemplateRequirement;
import com.pixurvival.core.contentPack.validation.annotation.AnimationTemplateRequirementSet;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Nullable;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.livingEntity.ability.Ability;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.ability.HarvestAbility;
import com.pixurvival.core.livingEntity.ability.SilenceAbility;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Creature extends NamedIdentifiedElement {

	private static final AbilitySet EMPTY_ABILITY_SET = new AbilitySet();

	static {
		initializeAbilitySet(null, EMPTY_ABILITY_SET);
	}

	private static final long serialVersionUID = 1L;

	@ElementReference
	@AnimationTemplateRequirement(AnimationTemplateRequirementSet.CHARACTER)
	private SpriteSheet spriteSheet;

	@Positive
	private float collisionRadius;

	private float strength;
	private float agility;
	private float intelligence;

	private boolean solid = true;

	@AbilityIndexes
	@ElementReference
	private BehaviorSet behaviorSet;

	@Nullable
	@ElementReference
	private ItemReward itemReward;

	@Nullable
	@ElementReference
	private AbilitySet abilitySet;

	@Positive
	private long lifetime;

	@Positive
	private int inventorySize;

	private transient @Setter(AccessLevel.NONE) int harvestAbilityId;

	private transient AbilitySet effectiveAbilitySet;

	@Override
	public void initialize() {
		if (abilitySet == null) {
			effectiveAbilitySet = EMPTY_ABILITY_SET;
			harvestAbilityId = 1;
		} else {
			effectiveAbilitySet = new AbilitySet();
			int newHarvestAbilityId = initializeAbilitySet(abilitySet, effectiveAbilitySet);
			if (newHarvestAbilityId != -1) {
				harvestAbilityId = newHarvestAbilityId;
			}
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

	private static int initializeAbilitySet(AbilitySet baseSet, AbilitySet targetSet) {
		targetSet.add(new SilenceAbility());
		if (baseSet != null) {
			List<Ability> abilities = baseSet.getAbilities();
			for (int i = 0; i < abilities.size(); i++) {
				targetSet.add(abilities.get(i).copy());
			}
		}
		return targetSet.add(new HarvestAbility());
	}
}
