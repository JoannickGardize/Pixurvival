package com.pixurvival.core.aliveEntity.creature;

import java.nio.ByteBuffer;

import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.aliveEntity.AliveEntity;
import com.pixurvival.core.aliveEntity.creature.ai.Behavior;

import lombok.Getter;
import lombok.Setter;

public class CreatureEntity extends AliveEntity {

	private @Getter @Setter Behavior currentBehavior;
	private @Getter @Setter double behaviorTime;

	@Override
	public double getMaxHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public EntityGroup getGroup() {
		// TODO Auto-generated method stub
		return null;
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
