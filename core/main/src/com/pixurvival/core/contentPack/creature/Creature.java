package com.pixurvival.core.contentPack.creature;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.ItemReward;
import com.pixurvival.core.livingEntity.ability.AbilitySet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Creature extends IdentifiedElement {

	private static final long serialVersionUID = 1L;

	private SpriteSheet spriteSheet;
	private double collisionRadius;

	private float strength;
	private float agility;
	private float intelligence;

	private BehaviorSet behaviorSet;

	private ItemReward itemReward;

	private AbilitySet abilitySet;
}
