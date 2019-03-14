package com.pixurvival.core.livingEntity;

import java.nio.ByteBuffer;

import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.contentPack.creature.BehaviorSet;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.livingEntity.ability.AbilitySet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class CreatureEntity extends LivingEntity {

	private @Getter @Setter Behavior currentBehavior;
	private @Getter @Setter BehaviorData behaviorData;
	private @Getter Creature definition;

	public void setAI(BehaviorSet ai) {
		currentBehavior = ai.getBehaviors().get(0);
		currentBehavior.begin(this);
	}

	public CreatureEntity(Creature definition) {
		this.definition = definition;
	}

	@Override
	public void update() {
		super.update();

		if (getWorld().isServer()) {
			currentBehavior.update(this);
		}
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.CREATURE;
	}

	@Override
	public double getBoundingRadius() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeUpdate(ByteBuffer buffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void applyUpdate(ByteBuffer buffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public AbilitySet getAbilitySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

}
