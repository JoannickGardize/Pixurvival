package com.pixurvival.contentPackEditor.component.creature;

import java.util.function.Predicate;

import com.pixurvival.core.contentPack.creature.BehaviorSet;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.validation.handler.AbilityIndexesHandler;

import lombok.Setter;

public class AbilityIndexesConstraint implements Predicate<BehaviorSet> {

	private @Setter Creature creature;

	@Override
	public boolean test(BehaviorSet t) {
		return AbilityIndexesHandler.test(creature);
	}

}
