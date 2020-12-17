package com.pixurvival.core.contentPack.creature;

import java.util.List;
import java.util.function.Consumer;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.item.ItemReward;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Nullable;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.livingEntity.ability.Ability;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.ability.HarvestAbility;
import com.pixurvival.core.livingEntity.ability.SilenceAbility;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.StatFormula;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Creature extends IdentifiedElement {

	private static final AbilitySet EMPTY_ABILITY_SET = new AbilitySet();

	static {
		initializeAbilitySet(EMPTY_ABILITY_SET);
	}

	private static final long serialVersionUID = 1L;

	@ElementReference
	private SpriteSheet spriteSheet;

	@Positive
	private float collisionRadius;

	private float strength;
	private float agility;
	private float intelligence;

	private boolean solid = true;

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

	@Override
	public void initialize() {
		if (abilitySet == null) {
			abilitySet = EMPTY_ABILITY_SET;
			harvestAbilityId = 1;
		} else {
			int newHarvestAbilityId = initializeAbilitySet(abilitySet);
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

	private static int initializeAbilitySet(AbilitySet abilitySet) {
		List<Ability> abilities = abilitySet.getAbilities();
		if (!abilities.isEmpty() && abilities.get(0) instanceof SilenceAbility) {
			return -1;
		}
		abilities.add(0, new SilenceAbility());
		for (int i = 0; i < abilities.size(); i++) {
			abilities.get(i).setId((byte) i);
		}

		return abilitySet.add(new HarvestAbility());
	}
}
