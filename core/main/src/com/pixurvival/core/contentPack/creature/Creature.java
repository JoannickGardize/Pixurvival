package com.pixurvival.core.contentPack.creature;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.item.ItemReward;
import com.pixurvival.core.livingEntity.ability.AbilitySet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Creature extends IdentifiedElement {

	private static final long serialVersionUID = 1L;

	@Required
	@ElementReference
	private SpriteSheet spriteSheet;

	@Bounds(min = 0)
	private double collisionRadius;

	private float strength;
	private float agility;
	private float intelligence;

	@Required
	@ElementReference
	private BehaviorSet behaviorSet;

	@ElementReference
	private ItemReward itemReward;

	@ElementReference
	private AbilitySet abilitySet;
}
