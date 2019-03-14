package com.pixurvival.core.contentPack.creature;

import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.item.ItemReward;

import lombok.Data;

@Data
public class Creature {

	private SpriteSheet spriteSheet;
	private double collisionRadius;

	private float strength;
	private float agility;
	private float intelligence;

	private BehaviorSet behaviorSet;

	private ItemReward itemReward;
}
