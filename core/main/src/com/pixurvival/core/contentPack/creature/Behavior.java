package com.pixurvival.core.contentPack.creature;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.livingEntity.ability.Ability;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class Behavior extends IdentifiedElement {

	private static final long serialVersionUID = 1L;

	@Valid
	private List<ChangeCondition> changeConditions = new ArrayList<>();

	@Bounds(min = Ability.NONE_ID)
	private int abilityToUseId = Ability.NONE_ID;

	public void update(CreatureEntity creature) {
		BehaviorData behaviorData = creature.getBehaviorData();
		if (creature.getWorld().getTime().getTimeMillis() >= behaviorData.getNextUpdateTimeMillis()) {
			nextBehavior(creature);
			behaviorData.beforeStep();
			step(creature);
		}
		if (behaviorData.mustCheckChangeCondition()) {
			nextBehavior(creature);
		}
	}

	public void begin(CreatureEntity creature) {
		creature.setBehaviorData(new BehaviorData(creature));
		creature.startAbility(abilityToUseId);
	}

	protected abstract void step(CreatureEntity creature);

	protected void end(CreatureEntity creature) {
		// Nothing by default
	}

	private void nextBehavior(CreatureEntity creature) {
		for (ChangeCondition condition : changeConditions) {
			if (condition.test(creature)) {
				pass(creature, condition.getNextBehavior());
				break;
			}
		}
		creature.getBehaviorData().updatePreviousChangeConditionCheck();
	}

	private void pass(CreatureEntity creature, Behavior nextBehavior) {
		end(creature);
		creature.setCurrentBehavior(nextBehavior);
		nextBehavior.begin(creature);
	}

}
