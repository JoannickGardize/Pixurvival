package com.pixurvival.core.aliveEntity.creature;

import java.nio.ByteBuffer;

import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.aliveEntity.AliveEntity;
import com.pixurvival.core.contentPack.ai.ArtificialIntelligence;
import com.pixurvival.core.contentPack.ai.Behavior;
import com.pixurvival.core.contentPack.ai.BehaviorData;

import lombok.Getter;
import lombok.Setter;

public class CreatureEntity extends AliveEntity {

	private @Getter @Setter Behavior currentBehavior;
	private @Getter @Setter BehaviorData behaviorData;

	public void setAI(ArtificialIntelligence ai) {
		currentBehavior = ai.getBehaviors().get(0);
		currentBehavior.begin(this);
	}

	@Override
	public double getMaxHealth() {
		return 0;
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
	public double getSpeedPotential() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isSolid() {
		// TODO Auto-generated method stub
		return false;
	}

}
