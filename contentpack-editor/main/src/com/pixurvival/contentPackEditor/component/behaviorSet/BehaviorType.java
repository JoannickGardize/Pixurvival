package com.pixurvival.contentPackEditor.component.behaviorSet;

import java.util.function.Supplier;

import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.GetAwayBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.MoveTowardBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.TurnAroundBehavior;
import com.pixurvival.core.contentPack.creature.behaviorImpl.WanderBehavior;
import com.pixurvival.core.util.CaseUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BehaviorType {

	GET_AWAY(GetAwayBehavior::new),
	MOVE_TOWARD(MoveTowardBehavior::new),
	TURN_AROUND(TurnAroundBehavior::new),
	WANDER(WanderBehavior::new);

	private @Getter Supplier<Behavior> behaviorSupplier;

	public static BehaviorType of(Class<? extends Behavior> type) {
		return valueOf(CaseUtils.camelToUpperCase(type.getSimpleName().substring(0, type.getSimpleName().length() - 8)));
	}

	public static BehaviorType of(Behavior type) {
		return of(type.getClass());
	}

	@Override
	public String toString() {
		return TranslationService.getInstance().getString("behaviorType." + CaseUtils.upperToCamelCase(name()));
	}
}