package com.pixurvival.core.contentPack.creature;

import java.util.List;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Setter;

@Setter
public abstract class Behavior extends IdentifiedElement {

	private static final long serialVersionUID = 1L;

	private List<ChangeCondition> changeConditions;

	public void update(CreatureEntity creature) {
		creature.getBehaviorData().beforeStep();
		step(creature);
		nextBehavior(creature);
	}

	public void begin(CreatureEntity creature) {
		creature.setBehaviorData(new BehaviorData(creature));
	}

	protected abstract void step(CreatureEntity creature);

	protected abstract void end(CreatureEntity creature);

	private void nextBehavior(CreatureEntity creature) {
		for (ChangeCondition condition : changeConditions) {
			if (condition.test(creature)) {
				pass(creature, condition.getNextBehavior());
				break;
			}
		}
	}

	private void pass(CreatureEntity creature, Behavior nextBehavior) {
		end(creature);
		creature.setCurrentBehavior(nextBehavior);
		nextBehavior.begin(creature);
	}

}
