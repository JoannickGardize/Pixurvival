package com.pixurvival.core.contentPack.effect;

import java.nio.ByteBuffer;

import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinearEffectMovement implements EffectMovement {

	private static final long serialVersionUID = 1L;

	private double speed;

	private boolean relative;

	@Override
	public void initialize(EffectEntity entity) {
		TeamMember ancestor = entity.getAncestor();
		entity.getPosition().set(ancestor.getPosition());
		entity.setForward(true);
	}

	@Override
	public void update(EffectEntity entity) {
		// Nothing
	}

	@Override
	public double getSpeedPotential(EffectEntity entity) {
		return speed;
	}

	@Override
	public void writeUpdate(ByteBuffer buffer, EffectEntity entity) {
		// Nothing
	}

	@Override
	public void applyUpdate(ByteBuffer buffer, EffectEntity entity) {
		// Nothing
	}
}
